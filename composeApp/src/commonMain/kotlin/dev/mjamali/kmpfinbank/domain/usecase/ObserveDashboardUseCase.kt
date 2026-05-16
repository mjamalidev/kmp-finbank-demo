package dev.mjamali.kmpfinbank.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import dev.mjamali.kmpfinbank.domain.common.Resource
import dev.mjamali.kmpfinbank.domain.mapper.toResource
import dev.mjamali.kmpfinbank.domain.model.Account
import dev.mjamali.kmpfinbank.domain.model.Transaction
import dev.mjamali.kmpfinbank.domain.repository.BankingRepository
import dev.mjamali.kmpfinbank.domain.result.Result

class LoadDashboardUseCase(
    private val repository: BankingRepository
) {

    operator fun invoke(): Flow<Resource<DashboardData>> = flow {
        emit(Resource.Loading)

        val accountsResult = repository.getAccounts()
        val transactionsResult = repository.getTransactions()

        when {
            accountsResult is Result.Error -> {
                emit(accountsResult.toResource())
            }

            transactionsResult is Result.Error -> {
                emit(transactionsResult.toResource())
            }

            accountsResult is Result.Success &&
                    transactionsResult is Result.Success -> {

                emit(
                    Resource.Success(
                        DashboardData(
                            accounts = accountsResult.data.orEmpty(),
                            recentTransactions = transactionsResult.data
                                .orEmpty()
                                .take(5)
                        )
                    )
                )
            }
        }
    }.catch { e ->
        emit(Resource.Exception(e))
    }
}

data class DashboardData(
    val accounts: List<Account>,
    val recentTransactions: List<Transaction>
)