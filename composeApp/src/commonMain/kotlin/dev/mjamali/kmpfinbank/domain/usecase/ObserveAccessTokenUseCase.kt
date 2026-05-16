package dev.mjamali.kmpfinbank.domain.usecase

import dev.mjamali.kmpfinbank.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class ObserveAccessTokenUseCase(
    private val authRepository: AuthRepository
) {

    operator fun invoke(): Flow<String?> {
        return authRepository.observeAccessToken()
    }
}