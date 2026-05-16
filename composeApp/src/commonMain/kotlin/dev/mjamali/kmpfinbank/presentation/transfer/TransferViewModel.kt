package dev.mjamali.kmpfinbank.presentation.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import dev.mjamali.kmpfinbank.domain.common.Resource
import dev.mjamali.kmpfinbank.domain.model.Transfer
import dev.mjamali.kmpfinbank.domain.usecase.GetLastReceiptUseCase
import dev.mjamali.kmpfinbank.domain.usecase.TransferMoneyUseCase
import dev.mjamali.kmpfinbank.domain.usecase.ValidateTransferUseCase
import dev.mjamali.kmpfinbank.domain.result.Result

class TransferViewModel(
    private val transferMoneyUseCase: TransferMoneyUseCase,
    private val validateTransferUseCase: ValidateTransferUseCase,
    private val getLastReceiptUseCase: GetLastReceiptUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransferUiState())
    val uiState: StateFlow<TransferUiState> = _uiState.asStateFlow()

    fun onFromAccountChanged(value: String) {
        _uiState.update {
            it.copy(
                fromAccountId = value,
                errorMessage = null
            )
        }
    }

    fun onToAccountChanged(value: String) {
        _uiState.update {
            it.copy(
                toAccountNumber = value,
                errorMessage = null
            )
        }
    }

    fun onAmountChanged(value: String) {
        _uiState.update {
            it.copy(
                amount = value,
                errorMessage = null
            )
        }
    }

    fun onNoteChanged(value: String) {
        _uiState.update {
            it.copy(
                note = value,
                errorMessage = null
            )
        }
    }

    fun onErrorConsumed() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }

    fun validateBeforeConfirmation(): Transfer? {
        val transfer = buildTransferOrSetError()
            ?: return null

        return when (val result = validateTransferUseCase(transfer)) {
            is Result.Success -> {
                _uiState.update {
                    it.copy(
                        confirmationTransfer = transfer,
                        errorMessage = null
                    )
                }

                transfer
            }

            is Result.Error -> {
                _uiState.update {
                    it.copy(
                        confirmationTransfer = null,
                        errorMessage = result.error.message ?: "Transfer data is invalid."
                    )
                }

                null
            }
        }
    }

    fun onSubmitTransferClicked() {
        val transfer = buildTransferOrSetError()
            ?: return

        viewModelScope.launch {
            transferMoneyUseCase(transfer).collect { resource ->
                when (resource) {
                    Resource.Loading -> {
                        _uiState.update {
                            it.copy(
                                isLoading = true,
                                errorMessage = null
                            )
                        }
                    }

                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                receipt = resource.data,
                                errorMessage = null
                            )
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = resource.message ?: "Transfer failed."
                            )
                        }
                    }

                    is Resource.Exception -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Transfer failed."
                            )
                        }
                    }
                }
            }
        }
    }

    fun loadLastReceipt() {
        viewModelScope.launch {
            getLastReceiptUseCase().collect { resource ->
                when (resource) {
                    Resource.Loading -> {
                        _uiState.update {
                            it.copy(
                                isLoading = true,
                                errorMessage = null
                            )
                        }
                    }

                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                receipt = resource.data,
                                errorMessage = null
                            )
                        }
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = resource.message ?: "Failed to load receipt."
                            )
                        }
                    }

                    is Resource.Exception -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Failed to load receipt."
                            )
                        }
                    }
                }
            }
        }
    }

    fun onReceiptHandled() {
        _uiState.update {
            it.copy(receipt = null)
        }
    }

    private fun buildTransferOrSetError(): Transfer? {
        val current = _uiState.value

        val amountMinor = parseAmountToMinorUnits(current.amount)

        if (amountMinor == null) {
            _uiState.update {
                it.copy(errorMessage = "Amount is invalid.")
            }

            return null
        }

        return Transfer(
            fromAccountId = current.fromAccountId,
            toAccountNumber = current.toAccountNumber,
            amountMinor = amountMinor,
            note = current.note.ifBlank { null }
        )
    }

    private fun parseAmountToMinorUnits(input: String): Long? {
        val normalized = input.trim().replace(",", "")

        if (normalized.isBlank()) return null

        val parts = normalized.split(".")

        if (parts.size > 2) return null

        val majorPart = parts[0]
        val minorPart = parts.getOrNull(1).orEmpty()

        if (majorPart.isBlank()) return null
        if (!majorPart.all { it.isDigit() }) return null
        if (!minorPart.all { it.isDigit() }) return null
        if (minorPart.length > 2) return null

        val major = majorPart.toLongOrNull() ?: return null
        val minor = minorPart.padEnd(2, '0').ifBlank { "00" }.toLong()

        return major * 100 + minor
    }
}