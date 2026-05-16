package dev.mjamali.kmpfinbank.domain.usecase

import dev.mjamali.kmpfinbank.domain.repository.SettingsRepository

class ObserveBalanceVisibilityUseCase(
    private val repository: SettingsRepository
) {
    operator fun invoke() = repository.observeBalanceVisible()
}