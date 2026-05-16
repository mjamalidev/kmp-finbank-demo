package dev.mjamali.kmpfinbank.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import dev.mjamali.kmpfinbank.domain.common.Resource
import dev.mjamali.kmpfinbank.domain.mapper.toResource
import dev.mjamali.kmpfinbank.domain.model.Receipt
import dev.mjamali.kmpfinbank.domain.model.Transfer
import dev.mjamali.kmpfinbank.domain.repository.BankingRepository
import dev.mjamali.kmpfinbank.domain.result.Result

class TransferMoneyUseCase(
    private val repository: BankingRepository,
    private val validateTransferUseCase: ValidateTransferUseCase
) {

    operator fun invoke(
        transfer: Transfer
    ): Flow<Resource<Receipt>> = flow {
        emit(Resource.Loading)

        val validationResult = validateTransferUseCase(transfer)

        if (validationResult is Result.Error) {
            emit(validationResult.toResource())
            return@flow
        }

        val transferResult = repository.transferMoney(transfer)

        emit(transferResult.toResource())
    }.catch { e ->
        emit(Resource.Exception(e))
    }
}