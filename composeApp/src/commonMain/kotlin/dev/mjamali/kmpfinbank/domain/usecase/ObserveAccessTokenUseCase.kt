package dev.mjamali.kmpfinbank.domain.usecase

import kotlinx.coroutines.flow.Flow
import dev.mjamali.kmpfinbank.domain.repository.AuthRepository

class ObserveAccessTokenUseCase(
    private val authRepository: AuthRepository
) {

    operator fun invoke(): Flow<String?> {
        return authRepository.observeAccessToken()
    }
}