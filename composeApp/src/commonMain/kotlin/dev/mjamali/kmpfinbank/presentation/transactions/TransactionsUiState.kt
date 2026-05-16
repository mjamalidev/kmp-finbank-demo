package dev.mjamali.kmpfinbank.presentation.transactions

import dev.mjamali.kmpfinbank.domain.model.Transaction

data class TransactionsUiState(
    val isLoading: Boolean = false,
    val transactions: List<Transaction> = emptyList(),
    val errorMessage: String? = null
)