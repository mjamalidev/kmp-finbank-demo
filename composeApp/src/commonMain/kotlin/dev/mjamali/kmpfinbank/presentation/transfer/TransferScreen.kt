package dev.mjamali.kmpfinbank.presentation.transfer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TransferScreen(
    onBack: () -> Unit,
    onConfirm: (
        fromAccountId: String,
        toAccountNumber: String,
        amount: Long,
        note: String?
    ) -> Unit,
    viewModel: TransferViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextButton(
            onClick = onBack,
            enabled = !uiState.isLoading
        ) {
            Text("Back")
        }

        Text("Transfer Money")

        OutlinedTextField(
            value = uiState.fromAccountId,
            onValueChange = viewModel::onFromAccountChanged,
            label = {
                Text("From Account ID")
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        )

        OutlinedTextField(
            value = uiState.toAccountNumber,
            onValueChange = viewModel::onToAccountChanged,
            label = {
                Text("Destination Account Number")
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        )

        OutlinedTextField(
            value = uiState.amount,
            onValueChange = viewModel::onAmountChanged,
            label = {
                Text("Amount")
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        )

        OutlinedTextField(
            value = uiState.note,
            onValueChange = viewModel::onNoteChanged,
            label = {
                Text("Note")
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        )

        uiState.errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            onClick = {
                val transfer = viewModel.validateBeforeConfirmation()

                if (transfer != null) {
                    onConfirm(
                        transfer.fromAccountId,
                        transfer.toAccountNumber,
                        transfer.amountMinor,
                        transfer.note
                    )
                }
            },
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Continue")
            }
        }
    }
}