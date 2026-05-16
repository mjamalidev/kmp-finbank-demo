package dev.mjamali.kmpfinbank.presentation.accounts

import dev.mjamali.kmpfinbank.domain.model.Account

data class AccountsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val accounts: List<Account> = emptyList(),
    val selectedAccount: Account? = null
)