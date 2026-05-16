package dev.mjamali.kmpfinbank.domain.usecase

import dev.mjamali.kmpfinbank.domain.common.Resource
import dev.mjamali.kmpfinbank.domain.model.Receipt
import dev.mjamali.kmpfinbank.domain.repository.BankingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetLastReceiptUseCase(
    private val repository: BankingRepository
) {

    operator fun invoke(): Flow<Resource<Receipt?>> = flow {
        emit(Resource.Loading)

        val receipt = repository.getLastReceipt()

        emit(Resource.Success(receipt))
    }.catch { e ->
        emit(Resource.Exception(e))
    }
}