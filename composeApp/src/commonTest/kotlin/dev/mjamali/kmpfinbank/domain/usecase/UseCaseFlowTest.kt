package dev.mjamali.kmpfinbank.domain.usecase

import dev.mjamali.kmpfinbank.data.remote.dto.LoginRequestDto
import dev.mjamali.kmpfinbank.domain.common.Resource
import dev.mjamali.kmpfinbank.domain.model.Account
import dev.mjamali.kmpfinbank.domain.model.ApiErrorModel
import dev.mjamali.kmpfinbank.domain.model.BankCard
import dev.mjamali.kmpfinbank.domain.model.Login
import dev.mjamali.kmpfinbank.domain.model.PaymentStatus
import dev.mjamali.kmpfinbank.domain.model.Receipt
import dev.mjamali.kmpfinbank.domain.model.Transaction
import dev.mjamali.kmpfinbank.domain.model.TransactionCategory
import dev.mjamali.kmpfinbank.domain.model.TransactionType
import dev.mjamali.kmpfinbank.domain.model.Transfer
import dev.mjamali.kmpfinbank.domain.repository.AuthRepository
import dev.mjamali.kmpfinbank.domain.repository.BankingRepository
import dev.mjamali.kmpfinbank.domain.repository.SettingsRepository
import dev.mjamali.kmpfinbank.domain.result.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UseCaseFlowTest {

    @Test
    fun getAccountsEmitsLoadingThenSuccess() = runTest {
        val accounts = listOf(account("acc_1"))
        val repository = FakeBankingRepository(accountsResult = Result.Success(accounts))

        val emissions = GetAccountsUseCase(repository)().toList()

        assertEquals(Resource.Loading, emissions[0])
        assertTrue(emissions[1] is Resource.Success)
        assertEquals(accounts, (emissions[1] as Resource.Success).data)
    }

    @Test
    fun getTransactionsEmitsRepositoryError() = runTest {
        val repository = FakeBankingRepository(
            transactionsResult = Result.Error(ApiErrorModel(503, "Unavailable"))
        )

        val emissions = GetTransactionsUseCase(repository)().toList()

        assertEquals(Resource.Loading, emissions[0])
        assertTrue(emissions[1] is Resource.Error)
        assertEquals("Unavailable", (emissions[1] as Resource.Error).message)
    }

    @Test
    fun getCardsConvertsRepositoryExceptionToExceptionResource() = runTest {
        val repository = FakeBankingRepository(
            cardsException = IllegalStateException("boom")
        )

        val emissions = GetCardsUseCase(repository)().toList()

        assertEquals(Resource.Loading, emissions[0])
        assertTrue(emissions[1] is Resource.Exception)
        assertEquals("boom", (emissions[1] as Resource.Exception).throwable.message)
    }

    @Test
    fun getAccountDetailsDelegatesAccountId() = runTest {
        val account = account("acc_2")
        val repository = FakeBankingRepository(accountDetailsResult = Result.Success(account))

        val emissions = GetAccountDetailsUseCase(repository)("acc_2").toList()

        assertEquals("acc_2", repository.lastAccountDetailsId)
        assertTrue(emissions[1] is Resource.Success)
        assertEquals(account, (emissions[1] as Resource.Success).data)
    }

    @Test
    fun loginAndRefreshEmitLoadingThenSuccess() = runTest {
        val authRepository = FakeAuthRepository(
            loginResult = Result.Success(Login("access", "refresh", "Demo")),
            refreshResult = Result.Success(Login("new-access", null, "Demo"))
        )

        val login = LoginUseCase(authRepository)(
            LoginRequestDto(username = "demo", password = "secret")
        ).toList()
        val refresh = RefreshLoginUseCase(authRepository)("refresh").toList()

        assertEquals("demo", authRepository.lastLoginRequest?.username)
        assertEquals("refresh", authRepository.lastRefreshToken)
        assertTrue(login[1] is Resource.Success)
        assertEquals("access", (login[1] as Resource.Success).data?.accessToken)
        assertTrue(refresh[1] is Resource.Success)
        assertEquals("new-access", (refresh[1] as Resource.Success).data?.accessToken)
    }

    @Test
    fun loadDashboardTakesFiveRecentTransactions() = runTest {
        val transactions = (1..7).map { transaction("tx_$it") }
        val repository = FakeBankingRepository(
            accountsResult = Result.Success(listOf(account("acc_1"))),
            transactionsResult = Result.Success(transactions)
        )

        val emissions = LoadDashboardUseCase(repository)().toList()

        assertEquals(Resource.Loading, emissions[0])
        assertTrue(emissions[1] is Resource.Success)
        val data = (emissions[1] as Resource.Success).data
        assertEquals(1, data?.accounts?.size)
        assertEquals(transactions.take(5), data?.recentTransactions)
    }

    @Test
    fun loadDashboardEmitsAccountsErrorBeforeTransactionsResult() = runTest {
        val repository = FakeBankingRepository(
            accountsResult = Result.Error(ApiErrorModel(401, "Unauthorized"))
        )

        val emissions = LoadDashboardUseCase(repository)().toList()

        assertTrue(emissions[1] is Resource.Error)
        assertEquals("Unauthorized", (emissions[1] as Resource.Error).message)
        assertEquals(1, repository.getTransactionsCalls)
    }

    @Test
    fun transferMoneyDoesNotCallRepositoryWhenValidationFails() = runTest {
        val repository = FakeBankingRepository()
        val invalidTransfer = transfer(amountMinor = 0)

        val emissions = TransferMoneyUseCase(repository, ValidateTransferUseCase())(invalidTransfer).toList()

        assertTrue(emissions[1] is Resource.Error)
        assertEquals("Amount must be greater than zero.", (emissions[1] as Resource.Error).message)
        assertEquals(0, repository.transferMoneyCalls)
    }

    @Test
    fun transferMoneyCallsRepositoryForValidTransfer() = runTest {
        val receipt = receipt()
        val repository = FakeBankingRepository(transferResult = Result.Success(receipt))

        val emissions = TransferMoneyUseCase(repository, ValidateTransferUseCase())(transfer()).toList()

        assertEquals(1, repository.transferMoneyCalls)
        assertTrue(emissions[1] is Resource.Success)
        assertEquals(receipt, (emissions[1] as Resource.Success).data)
    }

    @Test
    fun simpleDelegatingUseCasesCallRepositories() = runTest {
        val authRepository = FakeAuthRepository()
        val settingsRepository = FakeSettingsRepository()

        LogoutUseCase(authRepository)()
        ToggleBalanceVisibilityUseCase(settingsRepository)(currentValue = true)
        UpdateLastActiveAtUseCase(settingsRepository)(timestamp = 123L)

        assertTrue(authRepository.logoutCalled)
        assertEquals(false, settingsRepository.balanceVisible.value)
        assertEquals(123L, settingsRepository.lastActiveAt.value)
        assertEquals("token", ObserveAccessTokenUseCase(authRepository)().first())
        assertTrue(ObserveHasLocalSessionUseCase(authRepository)().first())
        assertFalse(ObserveBalanceVisibilityUseCase(settingsRepository)().first())
    }

    @Test
    fun getLastReceiptEmitsStoredReceipt() = runTest {
        val receipt = receipt()
        val repository = FakeBankingRepository(lastReceipt = receipt)

        val emissions = GetLastReceiptUseCase(repository)().toList()

        assertEquals(Resource.Loading, emissions[0])
        assertTrue(emissions[1] is Resource.Success)
        assertEquals(receipt, (emissions[1] as Resource.Success).data)
    }

    private class FakeAuthRepository(
        private val loginResult: Result<Login, ApiErrorModel> = Result.Success(Login("token", "refresh", "Demo")),
        private val refreshResult: Result<Login, ApiErrorModel> = Result.Success(Login("token", null, "Demo"))
    ) : AuthRepository {
        var lastLoginRequest: LoginRequestDto? = null
        var lastRefreshToken: String? = null
        var logoutCalled = false
        private val accessToken = MutableStateFlow<String?>("token")
        private val hasLocalSession = MutableStateFlow(true)

        override suspend fun login(request: LoginRequestDto): Result<Login, ApiErrorModel> {
            lastLoginRequest = request
            return loginResult
        }

        override suspend fun refreshLogin(refreshToken: String): Result<Login, ApiErrorModel> {
            lastRefreshToken = refreshToken
            return refreshResult
        }

        override suspend fun logout() {
            logoutCalled = true
        }

        override fun observeAccessToken(): Flow<String?> = accessToken

        override fun observeHasLocalSession(): Flow<Boolean> = hasLocalSession
    }

    private class FakeSettingsRepository : SettingsRepository {
        val balanceVisible = MutableStateFlow(true)
        val lastActiveAt = MutableStateFlow(0L)

        override fun observeBalanceVisible(): Flow<Boolean> = balanceVisible

        override suspend fun setBalanceVisible(visible: Boolean) {
            balanceVisible.value = visible
        }

        override fun observeLastActiveAt(): Flow<Long> = lastActiveAt

        override suspend fun updateLastActiveAt(timestamp: Long) {
            lastActiveAt.value = timestamp
        }
    }

    private class FakeBankingRepository(
        private val accountsResult: Result<List<Account>, ApiErrorModel> = Result.Success(emptyList()),
        private val accountDetailsResult: Result<Account, ApiErrorModel> = Result.Success(account("acc_1")),
        private val cardsResult: Result<List<BankCard>, ApiErrorModel> = Result.Success(emptyList()),
        private val cardsException: Throwable? = null,
        private val transactionsResult: Result<List<Transaction>, ApiErrorModel> = Result.Success(emptyList()),
        private val transferResult: Result<Receipt, ApiErrorModel> = Result.Success(receipt()),
        private val lastReceipt: Receipt? = null
    ) : BankingRepository {
        var lastAccountDetailsId: String? = null
        var getTransactionsCalls = 0
        var transferMoneyCalls = 0

        override suspend fun getAccounts(): Result<List<Account>, ApiErrorModel> = accountsResult

        override suspend fun getAccountDetails(accountId: String): Result<Account, ApiErrorModel> {
            lastAccountDetailsId = accountId
            return accountDetailsResult
        }

        override suspend fun getCards(): Result<List<BankCard>, ApiErrorModel> {
            cardsException?.let { throw it }
            return cardsResult
        }

        override suspend fun getTransactions(): Result<List<Transaction>, ApiErrorModel> {
            getTransactionsCalls += 1
            return transactionsResult
        }

        override suspend fun transferMoney(transfer: Transfer): Result<Receipt, ApiErrorModel> {
            transferMoneyCalls += 1
            return transferResult
        }

        override suspend fun getLastReceipt(): Receipt? = lastReceipt
    }
}

private fun account(id: String) = Account(
    id = id,
    name = "Main Account",
    iban = "GB00",
    balanceMinor = 10_000,
    currency = "USD"
)

private fun transaction(id: String) = Transaction(
    id = id,
    accountId = "acc_1",
    title = "Payment",
    amountMinor = 1_000,
    category = TransactionCategory.Transfer,
    date = "2026-05-01",
    type = TransactionType.Expense
)

private fun transfer(amountMinor: Long = 1_000) = Transfer(
    fromAccountId = "acc_1",
    toAccountNumber = "123456789",
    amountMinor = amountMinor,
    note = "Rent"
)

private fun receipt() = Receipt(
    id = "receipt_1",
    trackingCode = "TRX-1",
    fromAccountId = "acc_1",
    toAccountNumber = "123456789",
    amountMinor = 1_000,
    date = "2026-05-01",
    status = PaymentStatus.Success
)
