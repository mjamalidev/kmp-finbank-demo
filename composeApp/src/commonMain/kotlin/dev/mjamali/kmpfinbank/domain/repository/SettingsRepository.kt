package dev.mjamali.kmpfinbank.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeBalanceVisible(): Flow<Boolean>
    suspend fun setBalanceVisible(visible: Boolean)

    fun observeLastActiveAt(): Flow<Long>
    suspend fun updateLastActiveAt(timestamp: Long)
}