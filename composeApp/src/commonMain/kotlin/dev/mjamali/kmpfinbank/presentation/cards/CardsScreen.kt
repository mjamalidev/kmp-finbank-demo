package dev.mjamali.kmpfinbank.presentation.cards

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
import dev.mjamali.kmpfinbank.presentation.components.ErrorStateView
import dev.mjamali.kmpfinbank.presentation.components.LoadingView
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CardsScreen(
    onBack: () -> Unit,
    viewModel: CardsViewModel = koinViewModel()
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
            CardsContent(
                uiState = uiState,
                onBack = onBack
            )
        }
    }
}

@Composable
private fun CardsContent(
    uiState: CardsUiState,
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

        Text("Cards")

        uiState.cards.forEach { card ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(card.holderName)
                    Text(card.maskedNumber)
                    Text("Expiry: ${card.expiry}")
                    Text("Type: ${card.type}")
                    Text(
                        text = if (card.isFrozen) {
                            "Frozen"
                        } else {
                            "Active"
                        }
                    )
                }
            }
        }
    }
}