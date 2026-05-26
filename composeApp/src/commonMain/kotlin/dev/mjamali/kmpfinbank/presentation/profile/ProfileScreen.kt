package dev.mjamali.kmpfinbank.presentation.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.mjamali.kmpfinbank.biometric.rememberBiometricLoginController
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val biometricLoginController = rememberBiometricLoginController()

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            biometricLoginController.clearStoredRefreshToken()
            onLogout()
            viewModel.onLogoutHandled()
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

        Text("Profile / Settings")
        Text("User: KMP Demo User")
        Text("Email: demo@kmpbank.dev")

        Text("Show balance")

        Switch(
            checked = uiState.isBalanceVisible,
            onCheckedChange = {
                viewModel.onToggleBalanceVisibilityClicked()
            },
            enabled = !uiState.isLoading
        )

        uiState.errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = viewModel::onLogoutClicked,
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Logout")
            }
        }
    }
}