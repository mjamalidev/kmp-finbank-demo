package dev.mjamali.kmpfinbank.biometric

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object BiometricRefreshTokenVault {

    private const val PREFS = "biometric_refresh_token_vault"
    private const val KEY_CT = "refresh_token_ct_b64"
    private const val KEY_IV = "refresh_token_iv_b64"

    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "refresh_token_biometric_key"

    fun isBiometricAvailable(context: Context): Boolean {
        val manager = BiometricManager.from(context)

        return manager.canAuthenticate(
            Authenticators.BIOMETRIC_STRONG
        ) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun hasStoredRefreshToken(context: Context): Boolean {
        val prefs = context.getSharedPreferences(
            PREFS,
            Context.MODE_PRIVATE
        )

        val ciphertext = prefs.getString(KEY_CT, null)
        val iv = prefs.getString(KEY_IV, null)

        return !ciphertext.isNullOrBlank() && !iv.isNullOrBlank()
    }

    fun canUseBiometrics(context: Context): Boolean {
        return isBiometricAvailable(context) && hasStoredRefreshToken(context)
    }

    fun clearStoredRefreshToken(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()

        runCatching {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
                load(null)
            }

            if (keyStore.containsAlias(KEY_ALIAS)) {
                keyStore.deleteEntry(KEY_ALIAS)
            }
        }
    }

    fun getCipherForEncryption(): Cipher {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")

        cipher.init(
            Cipher.ENCRYPT_MODE,
            getOrCreateSecretKey()
        )

        return cipher
    }

    fun storeEncryptedRefreshToken(
        context: Context,
        cipher: Cipher,
        refreshToken: String
    ) {
        val ciphertext = cipher.doFinal(refreshToken.encodeToByteArray())
        val iv = cipher.iv

        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_CT, Base64.encodeToString(ciphertext, Base64.NO_WRAP))
            .putString(KEY_IV, Base64.encodeToString(iv, Base64.NO_WRAP))
            .apply()
    }

    fun getCipherForDecryption(context: Context): Cipher {
        val prefs = context.getSharedPreferences(
            PREFS,
            Context.MODE_PRIVATE
        )

        val ivBase64 = prefs.getString(KEY_IV, null)
            ?: error("No biometric IV stored.")

        val iv = Base64.decode(ivBase64, Base64.NO_WRAP)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")

        cipher.init(
            Cipher.DECRYPT_MODE,
            getOrCreateSecretKey(),
            GCMParameterSpec(128, iv)
        )

        return cipher
    }

    fun decryptStoredRefreshToken(
        context: Context,
        cipher: Cipher
    ): String {
        val prefs = context.getSharedPreferences(
            PREFS,
            Context.MODE_PRIVATE
        )

        val ciphertextBase64 = prefs.getString(KEY_CT, null)
            ?: error("No biometric refresh token stored.")

        val ciphertext = Base64.decode(
            ciphertextBase64,
            Base64.NO_WRAP
        )

        return cipher.doFinal(ciphertext).decodeToString()
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
            load(null)
        }

        val existingKey = keyStore.getKey(KEY_ALIAS, null) as? SecretKey

        if (existingKey != null) {
            return existingKey
        }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )

        val specBuilder = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setKeySize(256)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(true)
            .setInvalidatedByBiometricEnrollment(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            specBuilder.setUserAuthenticationParameters(
                0,
                KeyProperties.AUTH_BIOMETRIC_STRONG
            )
        } else {
            @Suppress("DEPRECATION")
            specBuilder.setUserAuthenticationValidityDurationSeconds(-1)
        }

        keyGenerator.init(specBuilder.build())

        return keyGenerator.generateKey()
    }
}