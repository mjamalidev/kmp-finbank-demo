package dev.mjamali.kmpfinbank.presentation.accounts

import androidx.compose.foundation.clickable
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
fun AccountsScreen(
    onBack: () -> Unit,
    onAccountClick: (String) -> Unit,
    viewModel: AccountsViewModel = koinViewModel()
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
            AccountsContent(
                uiState = uiState,
                onBack = onBack,
                onAccountClick = onAccountClick
            )
        }
    }
}

@Composable
private fun AccountsContent(
    uiState: AccountsUiState,
    onBack: () -> Unit,
    onAccountClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("Back")
        }

        Text("Accounts")

        if (uiState.accounts.isEmpty()) {
            Text("No accounts found.")
        }

        uiState.accounts.forEach { account ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        onAccountClick(account.id)
                    }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(account.name)
                    Text(account.iban)
                    Text(
                        MoneyFormatter.format(
                            account.balanceMinor,
                            account.currency
                        )
                    )
                }
            }
        }
    }
}