package dev.mjamali.kmpfinbank.domain.repository

import dev.mjamali.kmpfinbank.data.remote.dto.LoginRequestDto
import dev.mjamali.kmpfinbank.domain.model.ApiErrorModel
import dev.mjamali.kmpfinbank.domain.model.Login
import dev.mjamali.kmpfinbank.domain.result.Result
import kotlinx.coroutines.flow.Flow


interface AuthRepository {

    suspend fun login(
        request: LoginRequestDto
    ): Result<Login, ApiErrorModel>

    suspend fun refreshLogin(
        refreshToken: String
    ): Result<Login, ApiErrorModel>

    suspend fun logout()

    fun observeAccessToken(): Flow<String?>

    fun observeHasLocalSession(): Flow<Boolean>
}