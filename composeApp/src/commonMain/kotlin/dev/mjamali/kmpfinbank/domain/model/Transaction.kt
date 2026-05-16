package dev.mjamali.kmpfinbank.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val id: String,
    val accountId: String,
    val title: String,
    val amountMinor: Long,
    val category: TransactionCategory,
    val date: String,
    val type: TransactionType
)

@Serializable
enum class TransactionCategory {
    Shopping,
    Food,
    Salary,
    Transfer,
    Bills,
    Transport,
    Entertainment,
    Other
}

@Serializable
enum class TransactionType {
    Income,
    Expense
}