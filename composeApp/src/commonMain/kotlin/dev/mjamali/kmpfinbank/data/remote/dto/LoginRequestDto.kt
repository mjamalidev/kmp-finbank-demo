package dev.mjamali.kmpfinbank.data.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class LoginRequestDto(
    val username: String,
    val password: String
)