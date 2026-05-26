package dev.mjamali.kmpfinbank.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import dev.mjamali.kmpfinbank.data.local.PreferenceKeys
import dev.mjamali.kmpfinbank.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    override fun observeBalanceVisible(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[PreferenceKeys.BALANCE_VISIBLE] ?: true
        }
    }

    override suspend fun setBalanceVisible(visible: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.BALANCE_VISIBLE] = visible
        }
    }

    override fun observeLastActiveAt(): Flow<Long> {
        return dataStore.data.map { preferences ->
            preferences[PreferenceKeys.LAST_ACTIVE_AT] ?: 0L
        }
    }

    override suspend fun updateLastActiveAt(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.LAST_ACTIVE_AT] = timestamp
        }
    }
}