package dev.mjamali.kmpfinbank.presentation.transfer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
fun ReceiptScreen(
    route: Route.Receipt,
    onDone: () -> Unit,
    viewModel: TransferViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(route.receiptId) {
        viewModel.loadLastReceipt()
    }

    val receipt = uiState.receipt

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Receipt")

        when {
            uiState.isLoading -> {
                CircularProgressIndicator()
            }

            uiState.errorMessage != null -> {
                Text(
                    text = uiState.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }

            receipt == null -> {
                Text("Receipt not found.")
            }

            else -> {
                Text("Status: ${receipt.status}")
                Text("Tracking Code: ${receipt.trackingCode}")
                Text("From: ${receipt.fromAccountId}")
                Text("To: ${receipt.toAccountNumber}")
                Text("Amount: ${MoneyFormatter.format(receipt.amountMinor)}")
                Text("Date: ${receipt.date}")
            }
        }

        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        ) {
            Text("Done")
        }
    }
}