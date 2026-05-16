package dev.mjamali.kmpfinbank.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import dev.mjamali.kmpfinbank.data.cache.LocalTransactionCache
import dev.mjamali.kmpfinbank.data.mapper.toDomain
import dev.mjamali.kmpfinbank.data.remote.dto.AccountDto
import dev.mjamali.kmpfinbank.data.remote.dto.CardDto
import dev.mjamali.kmpfinbank.data.remote.dto.ReceiptDto
import dev.mjamali.kmpfinbank.data.remote.dto.TransactionDto
import dev.mjamali.kmpfinbank.data.remote.dto.TransferRequestDto
import dev.mjamali.kmpfinbank.domain.model.Account
import dev.mjamali.kmpfinbank.domain.model.ApiErrorModel
import dev.mjamali.kmpfinbank.domain.model.BankCard
import dev.mjamali.kmpfinbank.domain.model.Receipt
import dev.mjamali.kmpfinbank.domain.model.Transaction
import dev.mjamali.kmpfinbank.domain.model.Transfer
import dev.mjamali.kmpfinbank.domain.repository.BankingRepository
import dev.mjamali.kmpfinbank.domain.repository.ReceiptRepository
import dev.mjamali.kmpfinbank.domain.result.Result

class BankingRepositoryImpl(
    private val httpClient: HttpClient,
    private val transactionCache: LocalTransactionCache,
    private val receiptRepository: ReceiptRepository
) : BankingRepository {

    override suspend fun getAccounts(): Result<List<Account>, ApiErrorModel> {
        return try {
            val response = httpClient.get("/accounts")

            if (response.status.isSuccess()) {
                val dto: List<AccountDto> = response.body()

                Result.Success(
                    dto.map { it.toDomain() }
                )
            } else {
                Result.Error(parseApiError(response))
            }
        } catch (e: Exception) {
            Result.Error(e.toNetworkApiError())
        }
    }

    override suspend fun getAccountDetails(
        accountId: String
    ): Result<Account, ApiErrorModel> {
        return try {
            val response = httpClient.get("/accounts")

            if (response.status.isSuccess()) {
                val dto: List<AccountDto> = response.body()

                val account = dto
                    .firstOrNull { it.id == accountId }
                    ?.toDomain()

                if (account != null) {
                    Result.Success(account)
                } else {
                    Result.Error(
                        ApiErrorModel(
                            code = 404,
                            message = "Account not found."
                        )
                    )
                }
            } else {
                Result.Error(parseApiError(response))
            }
        } catch (e: Exception) {
            Result.Error(e.toNetworkApiError())
        }
    }

    override suspend fun getCards(): Result<List<BankCard>, ApiErrorModel> {
        return try {
            val response = httpClient.get("/cards")

            if (response.status.isSuccess()) {
                val dto: List<CardDto> = response.body()

                Result.Success(
                    dto.map { it.toDomain() }
                )
            } else {
                Result.Error(parseApiError(response))
            }
        } catch (e: Exception) {
            Result.Error(e.toNetworkApiError())
        }
    }

    override suspend fun getTransactions(): Result<List<Transaction>, ApiErrorModel> {
        return try {
            val cached = transactionCache.getTransactions()

            val response = httpClient.get("/transactions")

            if (response.status.isSuccess()) {
                val dto: List<TransactionDto> = response.body()

                val remote = dto.map { it.toDomain() }

                transactionCache.saveTransactions(remote)

                Result.Success(remote)
            } else {
                if (cached.isNotEmpty()) {
                    Result.Success(cached)
                } else {
                    Result.Error(parseApiError(response))
                }
            }
        } catch (e: Exception) {
            val cached = transactionCache.getTransactions()

            if (cached.isNotEmpty()) {
                Result.Success(cached)
            } else {
                Result.Error(e.toNetworkApiError())
            }
        }
    }

    override suspend fun transferMoney(
        transfer: Transfer
    ): Result<Receipt, ApiErrorModel> {
        return try {
            val response = httpClient.post("/transfer") {
                setBody(
                    TransferRequestDto(
                        fromAccountId = transfer.fromAccountId,
                        toAccountNumber = transfer.toAccountNumber,
                        amountMinor = transfer.amountMinor,
                        note = transfer.note
                    )
                )
            }

            if (response.status.isSuccess()) {
                val dto: ReceiptDto = response.body()
                val receipt = dto.toDomain()

                receiptRepository.saveLastReceipt(receipt)

                Result.Success(receipt)
            } else {
                Result.Error(parseApiError(response))
            }
        } catch (e: Exception) {
            Result.Error(e.toNetworkApiError())
        }
    }

    override suspend fun getLastReceipt(): Receipt? {
        return receiptRepository.getLastReceipt()
    }

    private suspend fun parseApiError(
        response: HttpResponse
    ): ApiErrorModel {
        return try {
            response.body<ApiErrorModel>()
        } catch (_: Exception) {
            ApiErrorModel(
                code = response.status.value,
                message = "Server error (${response.status.description})"
            )
        }
    }

    private fun Throwable.toNetworkApiError(): ApiErrorModel {
        return ApiErrorModel(
            code = -1,
            message = message ?: "Network error"
        )
    }
}