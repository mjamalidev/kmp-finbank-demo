package dev.mjamali.kmpfinbank.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class TransactionDto(
    val id: String,
    val accountId: String,
    val title: String,
    val amountMinor: Long,
    val category: String,
    val date: String,
    val type: String
)