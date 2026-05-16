package dev.mjamali.kmpfinbank.domain.result

import dev.mjamali.kmpfinbank.domain.model.ApiErrorModel

sealed interface Result<out D : Any?, out E : ApiErrorModel> {
    data class Success<out D : Any?>(val data: D?) : Result<D, Nothing>
    data class Error<out E : ApiErrorModel>(val error: E) : Result<Nothing, E>
}

inline fun <T, E : ApiErrorModel, R> Result<T, E>.map(map: (T?) -> R): Result<R, E> {
    return when (this) {
        is Result.Error -> Result.Error(error)
        is Result.Success -> Result.Success(map(data))
    }
}

inline fun <T, E : ApiErrorModel> Result<T, E>.onSuccess(action: (T?) -> Unit): Result<T, E> {
    if (this is Result.Success) action(data)
    return this
}

inline fun <T, E : ApiErrorModel> Result<T, E>.onError(action: (E) -> Unit): Result<T, E> {
    if (this is Result.Error) action(error)
    return this
}