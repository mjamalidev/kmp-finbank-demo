package dev.mjamali.kmpfinbank.presentation.accounts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.mjamali.kmpfinbank.common.util.MoneyFormatter
import dev.mjamali.kmpfinbank.presentation.components.ErrorStateView
import dev.mjamali.kmpfinbank.presentation.components.LoadingView
import dev.mjamali.kmpfinbank.presentation.navigation.Route
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AccountDetailsScreen(
    onBack: () -> Unit,
    viewModel: AccountsViewModel = koinViewModel(),
    route: Route.AccountDetails
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(route.accountId) {
        viewModel.loadAccountDetails(route.accountId)
    }

    val selectedAccount = uiState.selectedAccount

    when {
        uiState.isLoading -> {
            LoadingView()
        }

        uiState.errorMessage != null -> {
            ErrorStateView(
                message = uiState.errorMessage ?: "",
                onRetry = {
                    viewModel.loadAccountDetails(route.accountId)
                }
            )
        }

        selectedAccount != null -> {
            AccountDetailsContent(
                accountName = selectedAccount.name,
                iban = selectedAccount.iban,
                balanceMinor = selectedAccount.balanceMinor,
                currency = selectedAccount.currency,
                onBack = onBack
            )
        }

        else -> {
            ErrorStateView(
                message = "Account not found.",
                onRetry = {
                    viewModel.loadAccountDetails(route.accountId)
                }
            )
        }
    }
}

@Composable
private fun AccountDetailsContent(
    accountName: String,
    iban: String,
    balanceMinor: Long,
    currency: String,
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

        Text("Account Details")

        Text("Name: $accountName")
        Text("IBAN: $iban")
        Text(
            text = "Balance: ${
                MoneyFormatter.format(
                    amountMinor = balanceMinor,
                    currency = currency
                )
            }"
        )
    }
}