package dev.mjamali.kmpfinbank.domain.usecase

import dev.mjamali.kmpfinbank.domain.repository.SettingsRepository

class UpdateLastActiveAtUseCase(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(timestamp: Long) {
        repository.updateLastActiveAt(timestamp)
    }
}