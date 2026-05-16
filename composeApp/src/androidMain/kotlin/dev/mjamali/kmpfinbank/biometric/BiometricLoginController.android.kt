package dev.mjamali.kmpfinbank.biometric

import android.security.keystore.KeyPermanentlyInvalidatedException
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import javax.crypto.AEADBadTagException

@Composable
actual fun rememberBiometricLoginController(): BiometricLoginController {
    val context = LocalContext.current
    val activity = context.findFragmentActivity()

    return remember(context, activity) {
        AndroidBiometricLoginController(
            context = context,
            activity = activity
        )
    }
}

private class AndroidBiometricLoginController(
    private val context: android.content.Context,
    private val activity: androidx.fragment.app.FragmentActivity?
) : BiometricLoginController {

    override val isSupported: Boolean
        get() = true

    override val isBiometricAvailable: Boolean
        get() = BiometricRefreshTokenVault.isBiometricAvailable(context)

    override val canUseBiometricLogin: Boolean
        get() = BiometricRefreshTokenVault.canUseBiometrics(context)

    override fun saveRefreshTokenWithBiometric(
        refreshToken: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val currentActivity = activity

        if (currentActivity == null) {
            onError("Biometric authentication requires a FragmentActivity.")
            return
        }

        if (!BiometricRefreshTokenVault.isBiometricAvailable(context)) {
            onError(BiometricMessages.AUTHENTICATION_NOT_AVAILABLE)
            return
        }

        val cipher = runCatching {
            BiometricRefreshTokenVault.getCipherForEncryption()
        }.getOrElse {
            onError("Could not prepare biometric login.")
            return
        }

        currentActivity.showBiometricCipherPrompt(
            title = "Enable biometric login",
            subtitle = "Use biometrics to protect your login session.",
            cipher = cipher,
            onResult = { result ->
                when (result) {
                    is BiometricPromptResult.Success -> {
                        runCatching {
                            BiometricRefreshTokenVault.storeEncryptedRefreshToken(
                                context = context,
                                cipher = result.cipher,
                                refreshToken = refreshToken
                            )
                        }.onSuccess {
                            onSuccess()
                        }.onFailure {
                            onError("Could not enable biometric login.")
                        }
                    }

                    BiometricPromptResult.Cancelled -> {
                        onError(BiometricMessages.SETUP_CANCELLED)
                    }

                    is BiometricPromptResult.Error -> {
                        onError(result.message)
                    }
                }
            }
        )
    }

    override fun unlockRefreshTokenWithBiometric(
        onSuccess: (refreshToken: String) -> Unit,
        onError: (String) -> Unit
    ) {
        val currentActivity = activity

        if (currentActivity == null) {
            onError("Biometric authentication requires a FragmentActivity.")
            return
        }

        if (!BiometricRefreshTokenVault.canUseBiometrics(context)) {
            onError(BiometricMessages.LOGIN_NOT_AVAILABLE)
            return
        }

        val cipher = runCatching {
            BiometricRefreshTokenVault.getCipherForDecryption(context)
        }.getOrElse { throwable ->
            BiometricRefreshTokenVault.clearStoredRefreshToken(context)
            onError(throwable.toUserFriendlyBiometricMessage())
            return
        }

        currentActivity.showBiometricCipherPrompt(
            title = "Biometric login",
            subtitle = "Authenticate to unlock your session.",
            cipher = cipher,
            onResult = { result ->
                when (result) {
                    is BiometricPromptResult.Success -> {
                        runCatching {
                            BiometricRefreshTokenVault.decryptStoredRefreshToken(
                                context = context,
                                cipher = result.cipher
                            )
                        }.onSuccess { refreshToken ->
                            onSuccess(refreshToken)
                        }.onFailure { throwable ->
                            BiometricRefreshTokenVault.clearStoredRefreshToken(context)
                            onError(throwable.toUserFriendlyBiometricMessage())
                        }
                    }

                    BiometricPromptResult.Cancelled -> {
                        // Important:
                        // Do not clear stored token.
                        // ViewModel will ignore this message.
                        onError(BiometricMessages.LOGIN_CANCELLED)
                    }

                    is BiometricPromptResult.Error -> {
                        // Do not clear stored token for normal prompt errors.
                        onError(result.message)
                    }
                }
            }
        )
    }

    override fun clearStoredRefreshToken() {
        BiometricRefreshTokenVault.clearStoredRefreshToken(context)
    }

    private fun Throwable.toUserFriendlyBiometricMessage(): String {
        return when (this) {
            is KeyPermanentlyInvalidatedException -> {
                BiometricMessages.SETTINGS_CHANGED
            }

            is AEADBadTagException -> {
                BiometricMessages.VERIFY_FAILED
            }

            else -> {
                BiometricMessages.LOGIN_INVALID
            }
        }
    }
}