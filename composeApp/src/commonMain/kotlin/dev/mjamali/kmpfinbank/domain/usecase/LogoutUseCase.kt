package dev.mjamali.kmpfinbank.domain.usecase

import dev.mjamali.kmpfinbank.domain.repository.AuthRepository

class LogoutUseCase(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke() {
        authRepository.logout()
    }
}