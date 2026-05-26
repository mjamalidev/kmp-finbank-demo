package dev.mjamali.kmpfinbank.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import dev.mjamali.kmpfinbank.data.local.PreferenceKeys
import dev.mjamali.kmpfinbank.data.mapper.toDomain
import dev.mjamali.kmpfinbank.data.remote.dto.LoginRequestDto
import dev.mjamali.kmpfinbank.data.remote.dto.LoginResponseDto
import dev.mjamali.kmpfinbank.data.remote.dto.RefreshTokenRequestDto
import dev.mjamali.kmpfinbank.data.remote.dto.RefreshTokenResponseDto
import dev.mjamali.kmpfinbank.domain.model.ApiErrorModel
import dev.mjamali.kmpfinbank.domain.model.Login
import dev.mjamali.kmpfinbank.domain.repository.AuthRepository
import dev.mjamali.kmpfinbank.domain.result.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.ExperimentalTime

class AuthRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val httpClient: HttpClient
) : AuthRepository {

    override suspend fun login(
        request: LoginRequestDto
    ): Result<Login, ApiErrorModel> {
        return try {
            val response = httpClient.post("/login") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            if (response.status.isSuccess()) {
                val dto: LoginResponseDto = response.body()

                // Store only the short-lived access token in normal session storage.
                saveAccessToken(dto.accessToken)

                Result.Success(dto.toDomain())
            } else {
                Result.Error(parseApiError(response))
            }
        } catch (e: Exception) {
            Result.Error(e.toNetworkApiError())
        }
    }

    override suspend fun refreshLogin(
        refreshToken: String
    ): Result<Login, ApiErrorModel> {
        return try {
            val response = httpClient.post("/refresh") {
                contentType(ContentType.Application.Json)
                setBody(
                    RefreshTokenRequestDto(
                        refreshToken = refreshToken
                    )
                )
            }

            if (response.status.isSuccess()) {
                val dto: RefreshTokenResponseDto = response.body()

                // Server returns only a new access token.
                // The refresh token stored behind biometric remains the same until server expiry.
                saveAccessToken(dto.accessToken)

                Result.Success(dto.toDomain())
            } else {
                Result.Error(parseApiError(response))
            }
        } catch (e: Exception) {
            Result.Error(e.toNetworkApiError())
        }
    }

    override suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.remove(PreferenceKeys.SESSION_TOKEN)
            preferences.remove(PreferenceKeys.LAST_ACTIVE_AT)
        }
    }

    override fun observeAccessToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[PreferenceKeys.SESSION_TOKEN]
        }
    }

    override fun observeHasLocalSession(): Flow<Boolean> {
        return observeAccessToken().map { accessToken ->
            !accessToken.isNullOrBlank()
        }
    }

    private suspend fun saveAccessToken(accessToken: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.SESSION_TOKEN] = accessToken
            preferences[PreferenceKeys.LAST_ACTIVE_AT] = currentTimeMillis()
        }
    }

    private suspend fun parseApiError(
        response: HttpResponse
    ): ApiErrorModel {
        return try {
            response.body<ApiErrorModel>()
        } catch (_: Exception) {
            ApiErrorModel(
                code = response.status.value,
                message = "Server error (${response.status.description})"
            )
        }
    }

    private fun Throwable.toNetworkApiError(): ApiErrorModel {
        return ApiErrorModel(
            code = -1,
            message = message ?: "Network error"
        )
    }

    @OptIn(ExperimentalTime::class)
    private fun currentTimeMillis(): Long {
        return kotlin.time.Clock.System.now().toEpochMilliseconds()
    }
}