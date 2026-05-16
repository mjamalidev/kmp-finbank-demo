package dev.mjamali.kmpfinbank.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardScreen(
    onAccountsClick: () -> Unit,
    onCardsClick: () -> Unit,
    onTransactionsClick: () -> Unit,
    onTransferClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSessionExpired: () -> Unit,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSessionExpired) {
        if (uiState.isSessionExpired) {
            onSessionExpired()
            viewModel.onSessionExpiredHandled()
        }
    }

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
            DashboardContent(
                uiState = uiState,
                onToggleBalanceVisibilityClick = viewModel::onToggleBalanceVisibilityClicked,
                onAccountsClick = onAccountsClick,
                onCardsClick = onCardsClick,
                onTransactionsClick = onTransactionsClick,
                onTransferClick = onTransferClick,
                onProfileClick = onProfileClick
            )
        }
    }
}

@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    onToggleBalanceVisibilityClick: () -> Unit,
    onAccountsClick: () -> Unit,
    onCardsClick: () -> Unit,
    onTransactionsClick: () -> Unit,
    onTransferClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Banking Dashboard")

        TotalBalanceCard(
            uiState = uiState,
            onToggleBalanceVisibilityClick = onToggleBalanceVisibilityClick
        )

        Button(
            onClick = onAccountsClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Accounts")
        }

        Button(
            onClick = onCardsClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cards")
        }

        Button(
            onClick = onTransactionsClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Transactions")
        }

        Button(
            onClick = onTransferClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Transfer Money")
        }

        TextButton(onClick = onProfileClick) {
            Text("Profile / Settings")
        }

        Text("Recent Transactions")

        uiState.recentTransactions.forEach { transaction ->
            Text(
                text = "${transaction.title} - ${
                    MoneyFormatter.format(transaction.amountMinor)
                }"
            )
        }
    }
}

@Composable
private fun TotalBalanceCard(
    uiState: DashboardUiState,
    onToggleBalanceVisibilityClick: () -> Unit
) {
    val totalBalanceMinor = uiState.accounts.sumOf { it.balanceMinor }
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Total Balance")

                TextButton(
                    onClick = onToggleBalanceVisibilityClick
                ) {
                    Text(
                        text = if (uiState.isBalanceVisible) {
                            "Hide"
                        } else {
                            "Show"
                        }
                    )
                }
            }

            Text(
                text = MoneyFormatter.format(
                    amountMinor = totalBalanceMinor,
                    currency = "USD",
                    visible = uiState.isBalanceVisible
                )
            )
        }
    }
}