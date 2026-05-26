package dev.mjamali.kmpfinbank.data.repository

import dev.mjamali.kmpfinbank.data.cache.LocalTransactionCache
import dev.mjamali.kmpfinbank.domain.model.PaymentStatus
import dev.mjamali.kmpfinbank.domain.model.Receipt
import dev.mjamali.kmpfinbank.domain.model.Transaction
import dev.mjamali.kmpfinbank.domain.model.TransactionCategory
import dev.mjamali.kmpfinbank.domain.model.TransactionType
import dev.mjamali.kmpfinbank.domain.model.Transfer
import dev.mjamali.kmpfinbank.domain.repository.ReceiptRepository
import dev.mjamali.kmpfinbank.domain.result.Result
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BankingRepositoryImplTest {

    @Test
    fun getAccountsMapsSuccessfulResponse() = runTest {
        val repository = repository(
            engine = MockEngine {
                respondJson(
                    """
                        [
                          {
                            "id": "acc_1",
                            "name": "Main Account",
                            "iban": "GB00",
                            "balanceMinor": 245075,
                            "currency": "USD"
                          }
                        ]
                    """.trimIndent()
                )
            }
        )

        val result = repository.getAccounts()

        assertTrue(result is Result.Success)
        assertEquals("acc_1", result.data?.single()?.id)
        assertEquals(245075, result.data?.single()?.balanceMinor)
    }

    @Test
    fun getAccountDetailsReturnsNotFoundForMissingAccount() = runTest {
        val repository = repository(
            engine = MockEngine {
                respondJson(
                    """
                        [
                          {
                            "id": "acc_1",
                            "name": "Main Account",
                            "iban": "GB00",
                            "balanceMinor": 245075,
                            "currency": "USD"
                          }
                        ]
                    """.trimIndent()
                )
            }
        )

        val result = repository.getAccountDetails("missing")

        assertTrue(result is Result.Error)
        assertEquals(404, result.error.code)
        assertEquals("Account not found.", result.error.message)
    }

    @Test
    fun getTransactionsSavesRemoteTransactionsToCache() = runTest {
        val cache = FakeTransactionCache()
        val repository = repository(
            cache = cache,
            engine = MockEngine {
                respondJson(
                    """
                        [
                          {
                            "id": "tx_1",
                            "accountId": "acc_1",
                            "title": "Salary",
                            "amountMinor": 500000,
                            "category": "Salary",
                            "date": "2026-05-01",
                            "type": "Income"
                          }
                        ]
                    """.trimIndent()
                )
            }
        )

        val result = repository.getTransactions()

        assertTrue(result is Result.Success)
        assertEquals("tx_1", result.data?.single()?.id)
        assertEquals(result.data, cache.transactions)
    }

    @Test
    fun getTransactionsReturnsCacheWhenServerFails() = runTest {
        val cached = listOf(transaction("cached_tx"))
        val repository = repository(
            cache = FakeTransactionCache(cached),
            engine = MockEngine {
                respondError(HttpStatusCode.InternalServerError)
            }
        )

        val result = repository.getTransactions()

        assertTrue(result is Result.Success)
        assertEquals(cached, result.data)
    }

    @Test
    fun getTransactionsReturnsServerErrorWhenCacheIsEmpty() = runTest {
        val repository = repository(
            engine = MockEngine {
                respondError(
                    status = HttpStatusCode.BadRequest,
                    content = """{"code":400,"message":"Invalid request"}""",
                    headers = jsonHeaders
                )
            }
        )

        val result = repository.getTransactions()

        assertTrue(result is Result.Error)
        assertEquals(400, result.error.code)
        assertEquals("Invalid request", result.error.message)
    }

    @Test
    fun transferMoneySavesReceiptAfterSuccessfulResponse() = runTest {
        val receiptRepository = FakeReceiptRepository()
        val repository = repository(
            receiptRepository = receiptRepository,
            engine = MockEngine {
                respondJson(
                    """
                        {
                          "id": "receipt_1",
                          "trackingCode": "TRX-1",
                          "fromAccountId": "acc_1",
                          "toAccountNumber": "123456789",
                          "amountMinor": 1000,
                          "date": "2026-05-01",
                          "status": "Success"
                        }
                    """.trimIndent()
                )
            }
        )

        val result = repository.transferMoney(
            Transfer(
                fromAccountId = "acc_1",
                toAccountNumber = "123456789",
                amountMinor = 1_000,
                note = null
            )
        )

        assertTrue(result is Result.Success)
        assertEquals(PaymentStatus.Success, result.data?.status)
        assertEquals(result.data, receiptRepository.lastReceipt)
    }

    private fun repository(
        engine: MockEngine,
        cache: LocalTransactionCache = FakeTransactionCache(),
        receiptRepository: ReceiptRepository = FakeReceiptRepository()
    ) = BankingRepositoryImpl(
        httpClient = HttpClient(engine) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                )
            }
            defaultRequest {
                url("https://bank.test")
                contentType(ContentType.Application.Json)
            }
        },
        transactionCache = cache,
        receiptRepository = receiptRepository
    )

    private class FakeTransactionCache(
        initialTransactions: List<Transaction> = emptyList()
    ) : LocalTransactionCache {
        var transactions: List<Transaction> = initialTransactions

        override suspend fun getTransactions(): List<Transaction> = transactions

        override suspend fun saveTransactions(transactions: List<Transaction>) {
            this.transactions = transactions
        }

        override suspend fun clear() {
            transactions = emptyList()
        }
    }

    private class FakeReceiptRepository : ReceiptRepository {
        var lastReceipt: Receipt? = null

        override suspend fun saveLastReceipt(receipt: Receipt) {
            lastReceipt = receipt
        }

        override suspend fun getLastReceipt(): Receipt? = lastReceipt
    }
}

private val jsonHeaders = headersOf(
    HttpHeaders.ContentType,
    ContentType.Application.Json.toString()
)

private fun io.ktor.client.engine.mock.MockRequestHandleScope.respondJson(
    content: String,
    status: HttpStatusCode = HttpStatusCode.OK
) = respond(
    content = content,
    status = status,
    headers = jsonHeaders
)

private fun transaction(id: String) = Transaction(
    id = id,
    accountId = "acc_1",
    title = "Cached",
    amountMinor = 1_000,
    category = TransactionCategory.Transfer,
    date = "2026-05-01",
    type = TransactionType.Expense
)
