package dev.mjamali.kmpfinbank.domain.usecase

import kotlinx.coroutines.flow.Flow
import dev.mjamali.kmpfinbank.domain.repository.AuthRepository

class ObserveHasLocalSessionUseCase(
    private val authRepository: AuthRepository
) {

    operator fun invoke(): Flow<Boolean> {
        return authRepository.observeHasLocalSession()
    }
}