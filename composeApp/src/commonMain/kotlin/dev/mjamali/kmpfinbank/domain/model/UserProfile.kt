package dev.mjamali.kmpfinbank.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String,
    val fullName: String,
    val phoneNumber: String,
    val email: String
)