package dev.mjamali.kmpfinbank.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import dev.mjamali.kmpfinbank.domain.common.Resource
import dev.mjamali.kmpfinbank.domain.mapper.toResource
import dev.mjamali.kmpfinbank.domain.model.Account
import dev.mjamali.kmpfinbank.domain.repository.BankingRepository

class GetAccountsUseCase(
    private val repository: BankingRepository
) {

    operator fun invoke(): Flow<Resource<List<Account>>> = flow {
        emit(Resource.Loading)

        val result = repository.getAccounts()

        emit(result.toResource())
    }.catch { e ->
        emit(Resource.Exception(e))
    }
}