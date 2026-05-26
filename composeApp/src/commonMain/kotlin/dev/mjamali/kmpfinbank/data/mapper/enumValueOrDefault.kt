package dev.mjamali.kmpfinbank.data.mapper

import dev.mjamali.kmpfinbank.domain.model.CardType
import dev.mjamali.kmpfinbank.domain.model.PaymentStatus
import dev.mjamali.kmpfinbank.domain.model.TransactionCategory
import dev.mjamali.kmpfinbank.domain.model.TransactionType

private inline fun <reified T : Enum<T>> enumValueOrDefault(
    rawValue: String,
    defaultValue: T
): T {
    return enumValues<T>().firstOrNull {
        it.name.equals(rawValue.trim(), ignoreCase = true)
    } ?: defaultValue
}

fun String.toCardTypeOrDefault(): CardType {
    return enumValueOrDefault(this, CardType.Debit)
}

fun String.toTransactionCategoryOrDefault(): TransactionCategory {
    return enumValueOrDefault(this, TransactionCategory.Other)
}

fun String.toTransactionTypeOrDefault(): TransactionType {
    return enumValueOrDefault(this, TransactionType.Expense)
}

fun String.toPaymentStatusOrDefault(): PaymentStatus {
    return enumValueOrDefault(this, PaymentStatus.Pending)
}