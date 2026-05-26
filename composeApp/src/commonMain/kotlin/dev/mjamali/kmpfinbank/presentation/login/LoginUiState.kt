package dev.mjamali.kmpfinbank.presentation.login

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    val biometricSetupRequest: BiometricSetupRequest? = null,
    val navigationRequest: HomeNavigationRequest? = null,
    val shouldClearStoredBiometricToken: Boolean = false
)

data class BiometricSetupRequest(
    val accessToken: String,
    val refreshToken: String,
    val userName: String
)

data class HomeNavigationRequest(
    val accessToken: String,
    val userName: String
)