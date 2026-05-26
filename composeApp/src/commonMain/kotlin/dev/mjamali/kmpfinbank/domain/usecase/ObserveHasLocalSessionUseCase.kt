package dev.mjamali.kmpfinbank.domain.usecase

import dev.mjamali.kmpfinbank.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class ObserveHasLocalSessionUseCase(
    private val authRepository: AuthRepository
) {

    operator fun invoke(): Flow<Boolean> {
        return authRepository.observeHasLocalSession()
    }
}