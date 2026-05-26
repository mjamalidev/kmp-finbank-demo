package dev.mjamali.kmpfinbank.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {

    @Serializable
    data object Login : Route

    @Serializable
    data object Dashboard : Route

    @Serializable
    data object Accounts : Route

    @Serializable
    data class AccountDetails(
        val accountId: String
    ) : Route

    @Serializable
    data object Cards : Route

    @Serializable
    data object Transactions : Route

    @Serializable
    data object Transfer : Route

    @Serializable
    data class PaymentConfirmation(
        val fromAccountId: String,
        val toAccountNumber: String,
        val amountMinor: Long,
        val note: String?
    ) : Route

    @Serializable
    data class Receipt(
        val receiptId: String
    ) : Route

    @Serializable
    data object Profile : Route
}