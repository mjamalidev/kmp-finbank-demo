package dev.mjamali.kmpfinbank.presentation.cards

import dev.mjamali.kmpfinbank.domain.model.BankCard

data class CardsUiState(
    val isLoading: Boolean = false,
    val cards: List<BankCard> = emptyList(),
    val errorMessage: String? = null
)