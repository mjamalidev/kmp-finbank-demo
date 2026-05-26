package dev.mjamali.kmpfinbank.domain.usecase

import dev.mjamali.kmpfinbank.domain.common.Resource
import dev.mjamali.kmpfinbank.domain.mapper.toResource
import dev.mjamali.kmpfinbank.domain.model.Transaction
import dev.mjamali.kmpfinbank.domain.repository.BankingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetTransactionsUseCase(
    private val repository: BankingRepository
) {

    operator fun invoke(): Flow<Resource<List<Transaction>>> = flow {
        emit(Resource.Loading)

        val result = repository.getTransactions()

        emit(result.toResource())
    }.catch { e ->
        emit(Resource.Exception(e))
    }
}