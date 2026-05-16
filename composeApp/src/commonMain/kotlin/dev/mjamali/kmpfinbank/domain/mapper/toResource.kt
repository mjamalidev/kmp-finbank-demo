package dev.mjamali.kmpfinbank.domain.mapper

import dev.mjamali.kmpfinbank.domain.common.Resource
import dev.mjamali.kmpfinbank.domain.model.ApiErrorModel
import dev.mjamali.kmpfinbank.domain.result.Result

fun <T> Result<T, ApiErrorModel>.toResource(): Resource<T> = when (this) {
    is Result.Success -> Resource.Success(data)
    is Result.Error -> Resource.Error(throwable = null, message = error.message)
}