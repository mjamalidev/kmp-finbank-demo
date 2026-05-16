package dev.mjamali.kmpfinbank.biometric

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberBiometricLoginController(): BiometricLoginController {
    return remember {
        IosNoOpBiometricLoginController()
    }
}

private class IosNoOpBiometricLoginController : BiometricLoginController {

    // iOS biometric authentication is intentionally not implemented in this demo version.
    override val isSupported: Boolean = false

    override val canUseBiometricLogin: Boolean = false

    override val isBiometricAvailable: Boolean = false

    override fun saveRefreshTokenWithBiometric(
        refreshToken: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        onError("Biometric login is not available on iOS yet.")
    }

    override fun unlockRefreshTokenWithBiometric(
        onSuccess: (refreshToken: String) -> Unit,
        onError: (String) -> Unit
    ) {
        onError("Biometric login is not available on iOS yet.")
    }

    override fun clearStoredRefreshToken() {
        // No-op for now.
    }
}