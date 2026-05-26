package dev.mjamali.kmpfinbank.data.mapper

import dev.mjamali.kmpfinbank.data.remote.dto.AccountDto
import dev.mjamali.kmpfinbank.data.remote.dto.CardDto
import dev.mjamali.kmpfinbank.data.remote.dto.LoginResponseDto
import dev.mjamali.kmpfinbank.data.remote.dto.ReceiptDto
import dev.mjamali.kmpfinbank.data.remote.dto.RefreshTokenResponseDto
import dev.mjamali.kmpfinbank.data.remote.dto.TransactionDto
import dev.mjamali.kmpfinbank.domain.model.Account
import dev.mjamali.kmpfinbank.domain.model.BankCard
import dev.mjamali.kmpfinbank.domain.model.Login
import dev.mjamali.kmpfinbank.domain.model.Receipt
import dev.mjamali.kmpfinbank.domain.model.Transaction

fun LoginResponseDto.toDomain(): Login {
    return Login(
        accessToken = accessToken,
        refreshToken = refreshToken,
        userName = userName
    )
}

fun RefreshTokenResponseDto.toDomain(): Login {
    return Login(
        accessToken = accessToken,
        refreshToken = null,
        userName = userName
    )
}

fun AccountDto.toDomain() = Account(
    id = id,
    name = name,
    iban = iban,
    balanceMinor = balanceMinor,
    currency = currency
)

fun CardDto.toDomain() = BankCard(
    id = id,
    holderName = holderName,
    cardNumber = cardNumber,
    expiry = expiry,
    type = type.toCardTypeOrDefault(),
    isFrozen = isFrozen
)

fun TransactionDto.toDomain() = Transaction(
    id = id,
    accountId = accountId,
    title = title,
    amountMinor = amountMinor,
    category = category.toTransactionCategoryOrDefault(),
    date = date,
    type = type.toTransactionTypeOrDefault()
)

fun ReceiptDto.toDomain() = Receipt(
    id = id,
    trackingCode = trackingCode,
    fromAccountId = fromAccountId,
    toAccountNumber = toAccountNumber,
    amountMinor = amountMinor,
    date = date,
    status = status.toPaymentStatusOrDefault()
)