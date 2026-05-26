package dev.mjamali.kmpfinbank.biometric

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import javax.crypto.Cipher

sealed interface BiometricPromptResult {
    data class Success(val cipher: Cipher) : BiometricPromptResult
    data object Cancelled : BiometricPromptResult
    data class Error(val message: String) : BiometricPromptResult
}

fun FragmentActivity.showBiometricCipherPrompt(
    title: String,
    subtitle: String,
    cipher: Cipher,
    onResult: (BiometricPromptResult) -> Unit
) {
    val executor = ContextCompat.getMainExecutor(this)

    val biometricPrompt = BiometricPrompt(
        this,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationSucceeded(
                result: BiometricPrompt.AuthenticationResult
            ) {
                val resultCipher = result.cryptoObject?.cipher

                if (resultCipher != null) {
                    onResult(BiometricPromptResult.Success(resultCipher))
                } else {
                    onResult(
                        BiometricPromptResult.Error(
                            BiometricMessages.VERIFY_FAILED
                        )
                    )
                }
            }

            override fun onAuthenticationError(
                errorCode: Int,
                errString: CharSequence
            ) {
                when (errorCode) {
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                    BiometricPrompt.ERROR_USER_CANCELED,
                    BiometricPrompt.ERROR_CANCELED -> {
                        onResult(BiometricPromptResult.Cancelled)
                    }

                    BiometricPrompt.ERROR_LOCKOUT,
                    BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {
                        onResult(
                            BiometricPromptResult.Error(
                                BiometricMessages.TEMPORARILY_LOCKED
                            )
                        )
                    }

                    else -> {
                        onResult(
                            BiometricPromptResult.Error(
                                errString.toString()
                            )
                        )
                    }
                }
            }

            override fun onAuthenticationFailed() {
                // User can retry inside the system biometric dialog.
            }
        }
    )

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle(title)
        .setSubtitle(subtitle)
        .setAllowedAuthenticators(
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        )
        .setNegativeButtonText("Cancel")
        .build()

    biometricPrompt.authenticate(
        promptInfo,
        BiometricPrompt.CryptoObject(cipher)
    )
}