package dev.mjamali.kmpfinbank.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Login(
    val accessToken: String,
    val refreshToken: String?,
    val userName: String
)