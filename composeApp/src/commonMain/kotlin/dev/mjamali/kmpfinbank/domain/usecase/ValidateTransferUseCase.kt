package dev.mjamali.kmpfinbank.domain.usecase

import dev.mjamali.kmpfinbank.domain.model.ApiErrorModel
import dev.mjamali.kmpfinbank.domain.model.Transfer
import dev.mjamali.kmpfinbank.domain.result.Result

class ValidateTransferUseCase {

    operator fun invoke(transfer: Transfer): Result<Transfer, ApiErrorModel> {
        if (transfer.fromAccountId.isBlank()) {
            return Result.Error(
                ApiErrorModel(
                    code = 400,
                    message = "Source account is required."
                )
            )
        }

        if (transfer.toAccountNumber.isBlank()) {
            return Result.Error(
                ApiErrorModel(
                    code = 400,
                    message = "Destination account is required."
                )
            )
        }

        if (transfer.amountMinor <= 0L) {
            return Result.Error(
                ApiErrorModel(
                    code = 400,
                    message = "Amount must be greater than zero."
                )
            )
        }

        return Result.Success(transfer)
    }
}