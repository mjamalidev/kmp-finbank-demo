package dev.mjamali.kmpfinbank.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TransferRequestDto(
    val fromAccountId: String,
    val toAccountNumber: String,
    val amountMinor: Long,
    val note: String?
)