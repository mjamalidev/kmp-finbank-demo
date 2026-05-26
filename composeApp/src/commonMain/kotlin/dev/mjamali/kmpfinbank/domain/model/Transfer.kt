package dev.mjamali.kmpfinbank.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Transfer(
    val fromAccountId: String,
    val toAccountNumber: String,
    val amountMinor: Long,
    val note: String?
)