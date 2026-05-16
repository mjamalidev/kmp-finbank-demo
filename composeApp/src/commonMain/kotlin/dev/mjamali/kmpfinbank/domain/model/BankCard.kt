package dev.mjamali.kmpfinbank.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class BankCard(
    val id: String,
    val holderName: String,
    val cardNumber: String,
    val expiry: String,
    val type: CardType,
    val isFrozen: Boolean
) {
    val maskedNumber: String
        get() = "**** **** **** ${cardNumber.takeLast(4)}"
}

@Serializable
enum class CardType {
    Debit,
    Credit,
    Virtual
}