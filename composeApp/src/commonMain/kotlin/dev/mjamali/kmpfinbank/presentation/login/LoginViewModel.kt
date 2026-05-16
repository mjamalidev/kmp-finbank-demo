package dev.mjamali.kmpfinbank.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import dev.mjamali.kmpfinbank.biometric.BiometricMessages
import dev.mjamali.kmpfinbank.data.remote.dto.LoginRequestDto
import dev.mjamali.kmpfinbank.domain.common.Resource
import dev.mjamali.kmpfinbank.domain.usecase.LoginUseCase
import dev.mjamali.kmpfinbank.domain.usecase.RefreshLoginUseCase

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val refreshLoginUseCase: RefreshLoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsernameChanged(value: String) {
        _uiState.update {
            it.copy(
                username = value,
                errorMessage = null
            )
        }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update {
            it.copy(
                password = value,
                errorMessage = null
            )
        }
    }

    fun onErrorConsumed() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }

    fun onLoginClicked() {
        viewModelScope.launch {
            val current = _uiState.value

            if (current.username.isBlank() || current.password.isBlank()) {
                _uiState.update {
                    it.copy(errorMessage = "Username and password required.")
                }
                return@launch
            }

            loginUseCase(
                LoginRequestDto(
                    username = current.username,
                    password = current.password
                )
            ).collect { resource ->
                when (resource) {
                    Resource.Loading -> {
                        _uiState.update {
                            it.copy(
                                isLoading = true,
                                errorMessage = null
                            )
                        }
                    }

                    is Resource.Success -> {
                        val data = resource.data

                        if (data == null) {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = "Empty response body."
                                )
                            }
                            return@collect
                        }

                        val refreshToken = data.refreshToken

                        if (refreshToken.isNullOrBlank()) {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = "Refresh token missing."
                                )
                            }
                            return@collect
                        }

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                biometricSetupRequest = BiometricSetupRequest(
                                    accessToken = data.accessToken,
                                    refreshToken = refreshToken,
                                    userName = data.userName
                                )
                            )
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = resource.message
                            )
                        }
                    }

                    is Resource.Exception -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Unexpected error."
                            )
                        }
                    }
                }
            }
        }
    }

    fun loginWithRefreshToken(refreshToken: String) {
        viewModelScope.launch {
            refreshLoginUseCase(refreshToken).collect { resource ->
                when (resource) {
                    Resource.Loading -> {
                        _uiState.update {
                            it.copy(
                                isLoading = true,
                                errorMessage = null
                            )
                        }
                    }

                    is Resource.Success -> {
                        val data = resource.data

                        if (data != null) {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    navigationRequest = HomeNavigationRequest(
                                        accessToken = data.accessToken,
                                        userName = data.userName
                                    )
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = "Empty response body."
                                )
                            }
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = resource.message
                                    ?: "Session expired. Please login again.",
                                shouldClearStoredBiometricToken = true
                            )
                        }
                    }

                    is Resource.Exception -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Biometric login failed."
                            )
                        }
                    }
                }
            }
        }
    }

    fun onBiometricLoginError(message: String) {
        if (
            message == BiometricMessages.LOGIN_CANCELLED ||
            message == BiometricMessages.SETUP_CANCELLED
        ) {
            _uiState.update {
                it.copy(isLoading = false)
            }
            return
        }

        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = message
            )
        }
    }

    fun onBiometricSetupCompleted() {
        val request = _uiState.value.biometricSetupRequest ?: return

        _uiState.update {
            it.copy(
                biometricSetupRequest = null,
                navigationRequest = HomeNavigationRequest(
                    accessToken = request.accessToken,
                    userName = request.userName
                )
            )
        }
    }

    fun onBiometricSetupSkipped() {
        val request = _uiState.value.biometricSetupRequest ?: return

        _uiState.update {
            it.copy(
                biometricSetupRequest = null,
                navigationRequest = HomeNavigationRequest(
                    accessToken = request.accessToken,
                    userName = request.userName
                )
            )
        }
    }

    fun onNavigationHandled() {
        _uiState.update {
            it.copy(navigationRequest = null)
        }
    }

    fun onClearStoredBiometricTokenHandled() {
        _uiState.update {
            it.copy(shouldClearStoredBiometricToken = false)
        }
    }
}