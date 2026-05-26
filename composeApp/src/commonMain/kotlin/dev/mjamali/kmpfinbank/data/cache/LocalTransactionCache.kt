package dev.mjamali.kmpfinbank.data.cache

import dev.mjamali.kmpfinbank.domain.model.Transaction

interface LocalTransactionCache {
    suspend fun getTransactions(): List<Transaction>
    suspend fun saveTransactions(transactions: List<Transaction>)
    suspend fun clear()
}