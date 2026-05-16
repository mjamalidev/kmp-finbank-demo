package dev.mjamali.kmpfinbank.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import dev.mjamali.kmpfinbank.data.local.PreferenceKeys
import dev.mjamali.kmpfinbank.domain.model.Receipt
import dev.mjamali.kmpfinbank.domain.repository.ReceiptRepository

class ReceiptRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val json: Json
) : ReceiptRepository {

    override suspend fun saveLastReceipt(receipt: Receipt) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.LAST_RECEIPT_JSON] =
                json.encodeToString(receipt)
        }
    }

    override suspend fun getLastReceipt(): Receipt? {
        val raw = dataStore.data
            .map { preferences -> preferences[PreferenceKeys.LAST_RECEIPT_JSON] }
            .first()

        if (raw.isNullOrBlank()) return null

        return runCatching {
            json.decodeFromString<Receipt>(raw)
        }.getOrNull()
    }
}