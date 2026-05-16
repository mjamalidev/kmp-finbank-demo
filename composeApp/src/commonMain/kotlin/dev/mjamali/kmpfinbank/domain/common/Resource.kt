package dev.mjamali.kmpfinbank.domain.common

sealed class Resource<out T> {

    data object Loading : Resource<Nothing>()

    data class Success<T>(val data: T?) : Resource<T>()

    data class Error(
        val throwable: Throwable? = null,
        val message: String? = null
    ) : Resource<Nothing>()

    data class Exception(val throwable: Throwable) : Resource<Nothing>()
}