package dev.mjamali.kmpfinbank.domain.model

import kotlinx.serialization.Serializable

@Serializable
open class ApiErrorModel(
    val code: Int? = null,
    val message: String? = null,
)