package dev.mjamali.kmpfinbank.data.cache

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import dev.mjamali.kmpfinbank.data.local.PreferenceKeys
import dev.mjamali.kmpfinbank.domain.model.Transaction
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class DataStoreTransactionCache(
    private val dataStore: DataStore<Preferences>,
    private val json: Json
) : LocalTransactionCache {

    override suspend fun getTransactions(): List<Transaction> {
        val raw = dataStore.data
            .map { preferences -> preferences[PreferenceKeys.TRANSACTIONS_CACHE_JSON] }
            .first()

        if (raw.isNullOrBlank()) return emptyList()

        return runCatching {
            json.decodeFromString<List<Transaction>>(raw)
        }.getOrDefault(emptyList())
    }

    override suspend fun saveTransactions(transactions: List<Transaction>) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.TRANSACTIONS_CACHE_JSON] =
                json.encodeToString(transactions)
        }
    }

    override suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.remove(PreferenceKeys.TRANSACTIONS_CACHE_JSON)
        }
    }
}