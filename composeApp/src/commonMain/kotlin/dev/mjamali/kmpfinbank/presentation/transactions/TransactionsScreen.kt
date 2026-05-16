package dev.mjamali.kmpfinbank.presentation.transactions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.mjamali.kmpfinbank.common.util.MoneyFormatter
import dev.mjamali.kmpfinbank.presentation.components.ErrorStateView
import dev.mjamali.kmpfinbank.presentation.components.LoadingView
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TransactionsScreen(
    onBack: () -> Unit,
    viewModel: TransactionsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> {
            LoadingView()
        }

        uiState.errorMessage != null -> {
            ErrorStateView(
                message = uiState.errorMessage ?: "",
                onRetry = viewModel::onRetryClicked
            )
        }

        else -> {
            TransactionsContent(
                uiState = uiState,
                onBack = onBack
            )
        }
    }
}

@Composable
private fun TransactionsContent(
    uiState: TransactionsUiState,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("Back")
        }

        Text("Transaction History")

        if (uiState.transactions.isEmpty()) {
            Text("No transactions yet.")
        }

        uiState.transactions.forEach { transaction ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(transaction.title)
                    Text(transaction.category.name)
                    Text(transaction.date)
                    Text(MoneyFormatter.format(transaction.amountMinor))
                    Text(transaction.type.name)
                }
            }
        }
    }
}