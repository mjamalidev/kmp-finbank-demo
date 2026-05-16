package dev.mjamali.kmpfinbank.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Receipt(
    val id: String,
    val trackingCode: String,
    val fromAccountId: String,
    val toAccountNumber: String,
    val amountMinor: Long,
    val date: String,
    val status: PaymentStatus
)

@Serializable
enum class PaymentStatus {
    Success,
    Failed,
    Pending
}