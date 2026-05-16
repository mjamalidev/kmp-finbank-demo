package dev.mjamali.kmpfinbank.data.cache

import dev.mjamali.kmpfinbank.domain.model.Transaction
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class TransactionCacheDataSource : LocalTransactionCache {

    private val mutex = Mutex()
    private var cachedTransactions: List<Transaction> = emptyList()

    override suspend fun getTransactions(): List<Transaction> {
        return mutex.withLock { cachedTransactions }
    }

    override suspend fun saveTransactions(transactions: List<Transaction>) {
        mutex.withLock {
            cachedTransactions = transactions
        }
    }

    override suspend fun clear() {
        mutex.withLock {
            cachedTransactions = emptyList()
        }
    }
}