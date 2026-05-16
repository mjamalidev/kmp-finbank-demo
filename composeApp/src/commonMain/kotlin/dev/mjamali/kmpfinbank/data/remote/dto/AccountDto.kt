package dev.mjamali.kmpfinbank.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AccountDto(
    val id: String,
    val name: String,
    val iban: String,
    val balanceMinor: Long,
    val currency: String
)