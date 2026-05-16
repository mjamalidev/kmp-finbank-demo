package dev.mjamali.kmpfinbank.biometric

object BiometricMessages {
    const val LOGIN_CANCELLED = "Biometric login was cancelled."
    const val SETUP_CANCELLED = "Biometric setup was cancelled."

    const val LOGIN_NOT_AVAILABLE =
        "Biometric login is not available. Please log in with your username and password first."

    const val AUTHENTICATION_NOT_AVAILABLE =
        "Biometric authentication is not available on this device."

    const val SETTINGS_CHANGED =
        "Biometric login was reset because your device biometric settings changed. Please log in with your username and password to enable it again."

    const val LOGIN_INVALID =
        "Biometric login is no longer valid. Please log in with your username and password to enable it again."

    const val VERIFY_FAILED =
        "Biometric login could not be verified. Please log in with your username and password to enable it again."

    const val TEMPORARILY_LOCKED =
        "Biometric login is temporarily locked. Please try again later or log in with your username and password."
}