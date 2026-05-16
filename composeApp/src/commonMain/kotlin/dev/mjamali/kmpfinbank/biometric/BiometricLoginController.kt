package dev.mjamali.kmpfinbank.biometric

import androidx.compose.runtime.Composable

interface BiometricLoginController {

    val isSupported: Boolean

    val canUseBiometricLogin: Boolean

    val isBiometricAvailable: Boolean

    fun saveRefreshTokenWithBiometric(
        refreshToken: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    fun unlockRefreshTokenWithBiometric(
        onSuccess: (refreshToken: String) -> Unit,
        onError: (String) -> Unit
    )

    fun clearStoredRefreshToken()
}

@Composable
expect fun rememberBiometricLoginController(): BiometricLoginController