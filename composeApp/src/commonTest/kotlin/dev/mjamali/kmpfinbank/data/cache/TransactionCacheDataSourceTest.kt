package dev.mjamali.kmpfinbank.data.cache

import dev.mjamali.kmpfinbank.domain.model.Transaction
import dev.mjamali.kmpfinbank.domain.model.TransactionCategory
import dev.mjamali.kmpfinbank.domain.model.TransactionType
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TransactionCacheDataSourceTest {

    @Test
    fun startsEmpty() = runTest {
        val cache = TransactionCacheDataSource()

        assertTrue(cache.getTransactions().isEmpty())
    }

    @Test
    fun savesAndReturnsTransactions() = runTest {
        val cache = TransactionCacheDataSource()
        val transactions = listOf(transaction("tx_1"), transaction("tx_2"))

        cache.saveTransactions(transactions)

        assertEquals(transactions, cache.getTransactions())
    }

    @Test
    fun clearRemovesTransactions() = runTest {
        val cache = TransactionCacheDataSource()
        cache.saveTransactions(listOf(transaction("tx_1")))

        cache.clear()

        assertTrue(cache.getTransactions().isEmpty())
    }

    private fun transaction(id: String) = Transaction(
        id = id,
        accountId = "acc_1",
        title = "Payment",
        amountMinor = 1_000,
        category = TransactionCategory.Transfer,
        date = "2026-05-01",
        type = TransactionType.Expense
    )
}
