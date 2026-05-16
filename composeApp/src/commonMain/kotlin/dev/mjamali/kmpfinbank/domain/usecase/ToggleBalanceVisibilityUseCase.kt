package dev.mjamali.kmpfinbank.domain.usecase

import dev.mjamali.kmpfinbank.domain.repository.SettingsRepository

class ToggleBalanceVisibilityUseCase(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(currentValue: Boolean) {
        repository.setBalanceVisible(!currentValue)
    }
}