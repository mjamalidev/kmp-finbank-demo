package dev.mjamali.kmpfinbank.data.mapper

import dev.mjamali.kmpfinbank.data.remote.dto.AccountDto
import dev.mjamali.kmpfinbank.data.remote.dto.CardDto
import dev.mjamali.kmpfinbank.data.remote.dto.LoginResponseDto
import dev.mjamali.kmpfinbank.data.remote.dto.ReceiptDto
import dev.mjamali.kmpfinbank.data.remote.dto.RefreshTokenResponseDto
import dev.mjamali.kmpfinbank.data.remote.dto.TransactionDto
import dev.mjamali.kmpfinbank.domain.model.CardType
import dev.mjamali.kmpfinbank.domain.model.PaymentStatus
import dev.mjamali.kmpfinbank.domain.model.TransactionCategory
import dev.mjamali.kmpfinbank.domain.model.TransactionType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MapperTest {

    @Test
    fun enumMappersIgnoreCaseAndWhitespace() {
        assertEquals(CardType.Virtual, " virtual ".toCardTypeOrDefault())
        assertEquals(TransactionCategory.Food, "food".toTransactionCategoryOrDefault())
        assertEquals(TransactionType.Income, " INCOME ".toTransactionTypeOrDefault())
        assertEquals(PaymentStatus.Success, "success".toPaymentStatusOrDefault())
    }

    @Test
    fun enumMappersReturnDomainDefaultsForUnknownValues() {
        assertEquals(CardType.Debit, "charge".toCardTypeOrDefault())
        assertEquals(TransactionCategory.Other, "health".toTransactionCategoryOrDefault())
        assertEquals(TransactionType.Expense, "refund".toTransactionTypeOrDefault())
        assertEquals(PaymentStatus.Pending, "queued".toPaymentStatusOrDefault())
    }

    @Test
    fun mapsAuthDtosToDomain() {
        val login = LoginResponseDto(
            accessToken = "access",
            refreshToken = "refresh",
            userName = "Demo User"
        ).toDomain()

        assertEquals("access", login.accessToken)
        assertEquals("refresh", login.refreshToken)
        assertEquals("Demo User", login.userName)

        val refresh = RefreshTokenResponseDto(
            accessToken = "new-access",
            userName = "Demo User"
        ).toDomain()

        assertEquals("new-access", refresh.accessToken)
        assertNull(refresh.refreshToken)
        assertEquals("Demo User", refresh.userName)
    }

    @Test
    fun mapsBankingDtosToDomain() {
        val account = AccountDto(
            id = "acc_1",
            name = "Main",
            iban = "GB00",
            balanceMinor = 12_345,
            currency = "USD"
        ).toDomain()

        assertEquals("acc_1", account.id)
        assertEquals(12_345, account.balanceMinor)

        val card = CardDto(
            id = "card_1",
            holderName = "Demo User",
            cardNumber = "4111111111111111",
            expiry = "12/28",
            type = "Credit",
            isFrozen = true
        ).toDomain()

        assertEquals(CardType.Credit, card.type)
        assertEquals("**** **** **** 1111", card.maskedNumber)

        val transaction = TransactionDto(
            id = "tx_1",
            accountId = "acc_1",
            title = "Salary",
            amountMinor = 50_000,
            category = "Salary",
            date = "2026-05-01",
            type = "Income"
        ).toDomain()

        assertEquals(TransactionCategory.Salary, transaction.category)
        assertEquals(TransactionType.Income, transaction.type)

        val receipt = ReceiptDto(
            id = "receipt_1",
            trackingCode = "TRX-1",
            fromAccountId = "acc_1",
            toAccountNumber = "1234",
            amountMinor = 10_000,
            date = "2026-05-02",
            status = "Failed"
        ).toDomain()

        assertEquals(PaymentStatus.Failed, receipt.status)
    }
}
