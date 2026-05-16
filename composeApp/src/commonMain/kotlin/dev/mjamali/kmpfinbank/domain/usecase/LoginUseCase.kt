package dev.mjamali.kmpfinbank.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import dev.mjamali.kmpfinbank.data.remote.dto.LoginRequestDto
import dev.mjamali.kmpfinbank.domain.common.Resource
import dev.mjamali.kmpfinbank.domain.mapper.toResource
import dev.mjamali.kmpfinbank.domain.model.Login
import dev.mjamali.kmpfinbank.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {

    operator fun invoke(
        request: LoginRequestDto
    ): Flow<Resource<Login>> = flow {
        emit(Resource.Loading)
        val result = authRepository.login(request)
        emit(result.toResource())
    }.catch { e ->
        emit(Resource.Exception(e))
    }
}