package dev.mjamali.kmpfinbank.presentation.profile

data class ProfileUiState(
    val isBalanceVisible: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedOut: Boolean = false
)