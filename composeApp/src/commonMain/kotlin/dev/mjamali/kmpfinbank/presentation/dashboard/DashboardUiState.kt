package dev.mjamali.kmpfinbank.presentation.dashboard

import dev.mjamali.kmpfinbank.domain.model.Account
import dev.mjamali.kmpfinbank.domain.model.Transaction

data class DashboardUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    val accounts: List<Account> = emptyList(),
    val recentTransactions: List<Transaction> = emptyList(),

    val isBalanceVisible: Boolean = true,

    val isSessionExpired: Boolean = false
)