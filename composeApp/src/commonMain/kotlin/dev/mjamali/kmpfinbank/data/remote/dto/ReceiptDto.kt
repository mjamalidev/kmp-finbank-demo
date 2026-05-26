package dev.mjamali.kmpfinbank.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReceiptDto(
    val id: String,
    val trackingCode: String,
    val fromAccountId: String,
    val toAccountNumber: String,
    val amountMinor: Long,
    val date: String,
    val status: String
)