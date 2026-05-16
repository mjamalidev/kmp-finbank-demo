package dev.mjamali.kmpfinbank.domain.usecase

import dev.mjamali.kmpfinbank.domain.common.Resource
import dev.mjamali.kmpfinbank.domain.mapper.toResource
import dev.mjamali.kmpfinbank.domain.model.Login
import dev.mjamali.kmpfinbank.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class RefreshLoginUseCase(
    private val authRepository: AuthRepository
) {

    operator fun invoke(
        refreshToken: String
    ): Flow<Resource<Login>> = flow {
        emit(Resource.Loading)
        val result = authRepository.refreshLogin(refreshToken)
        emit(result.toResource())
    }.catch { e ->
        emit(Resource.Exception(e))
    }
}