package dev.mjamali.kmpfinbank.presentation.transfer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.mjamali.kmpfinbank.common.util.MoneyFormatter
import dev.mjamali.kmpfinbank.presentation.navigation.Route
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PaymentConfirmationScreen(
    route: Route.PaymentConfirmation,
    onBack: () -> Unit,
    onPaymentSuccess: (String) -> Unit,
    viewModel: TransferViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val receipt = uiState.receipt

    LaunchedEffect(receipt?.id) {
        if (receipt != null) {
            onPaymentSuccess(receipt.id)
            viewModel.onReceiptHandled()
        }
    }

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

        Text("Payment Confirmation")

        Text("From: ${route.fromAccountId}")
        Text("To: ${route.toAccountNumber}")
        Text("Amount: ${MoneyFormatter.format(route.amountMinor)}")

        if (!route.note.isNullOrBlank()) {
            Text("Note: ${route.note}")
        }

        uiState.errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            onClick = {
                viewModel.onFromAccountChanged(route.fromAccountId)
                viewModel.onToAccountChanged(route.toAccountNumber)
                viewModel.onAmountChanged(
                    MoneyFormatter.formatInput(route.amountMinor)
                )
                viewModel.onNoteChanged(route.note.orEmpty())
                viewModel.onSubmitTransferClicked()
            },
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Confirm Payment")
            }
        }
    }
}