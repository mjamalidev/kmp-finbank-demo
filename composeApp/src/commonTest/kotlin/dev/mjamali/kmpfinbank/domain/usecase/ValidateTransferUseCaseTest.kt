package dev.mjamali.kmpfinbank.domain.usecase

import dev.mjamali.kmpfinbank.domain.model.Transfer
import dev.mjamali.kmpfinbank.domain.result.Result
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ValidateTransferUseCaseTest {

    private val useCase = ValidateTransferUseCase()

    @Test
    fun returnsSuccessForValidTransfer() {
        val transfer = transfer()

        val result = useCase(transfer)

        assertTrue(result is Result.Success)
        assertEquals(transfer, result.data)
    }

    @Test
    fun rejectsBlankSourceAccount() {
        val result = useCase(transfer(fromAccountId = " "))

        assertError(result, "Source account is required.")
    }

    @Test
    fun rejectsBlankDestinationAccount() {
        val result = useCase(transfer(toAccountNumber = " "))

        assertError(result, "Destination account is required.")
    }

    @Test
    fun rejectsNonPositiveAmount() {
        assertError(useCase(transfer(amountMinor = 0)), "Amount must be greater than zero.")
        assertError(useCase(transfer(amountMinor = -1)), "Amount must be greater than zero.")
    }

    private fun assertError(
        result: Result<Transfer, *>,
        message: String
    ) {
        assertTrue(result is Result.Error)
        assertEquals(400, result.error.code)
        assertEquals(message, result.error.message)
    }

    private fun transfer(
        fromAccountId: String = "acc_1",
        toAccountNumber: String = "123456789",
        amountMinor: Long = 1_000L
    ) = Transfer(
        fromAccountId = fromAccountId,
        toAccountNumber = toAccountNumber,
        amountMinor = amountMinor,
        note = "Rent"
    )
}
