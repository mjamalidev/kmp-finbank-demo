package dev.mjamali.kmpfinbank.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenResponseDto(
    val accessToken: String,
    val userName: String
)