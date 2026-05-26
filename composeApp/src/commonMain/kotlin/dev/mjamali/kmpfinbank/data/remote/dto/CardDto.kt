package dev.mjamali.kmpfinbank.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CardDto(
    val id: String,
    val holderName: String,
    val cardNumber: String,
    val expiry: String,
    val type: String,
    val isFrozen: Boolean
)