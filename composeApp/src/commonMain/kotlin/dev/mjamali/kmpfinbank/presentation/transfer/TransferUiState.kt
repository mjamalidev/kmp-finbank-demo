package dev.mjamali.kmpfinbank.presentation.transfer

import dev.mjamali.kmpfinbank.domain.model.Receipt
import dev.mjamali.kmpfinbank.domain.model.Transfer

data class TransferUiState(
    val fromAccountId: String = "",
    val toAccountNumber: String = "",
    val amount: String = "",
    val note: String = "",

    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    val receipt: Receipt? = null,
    val confirmationTransfer: Transfer? = null
)