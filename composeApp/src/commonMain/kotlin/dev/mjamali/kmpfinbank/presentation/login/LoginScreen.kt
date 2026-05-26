package dev.mjamali.kmpfinbank.presentation.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.mjamali.kmpfinbank.biometric.rememberBiometricLoginController
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val biometricLoginController = rememberBiometricLoginController()

    val biometricSetupRequest = uiState.biometricSetupRequest
    val navigationRequest = uiState.navigationRequest
    val shouldClearStoredBiometricToken = uiState.shouldClearStoredBiometricToken

    LaunchedEffect(biometricSetupRequest) {
        val request = biometricSetupRequest ?: return@LaunchedEffect

        if (
            biometricLoginController.isSupported &&
            biometricLoginController.isBiometricAvailable
        ) {
            biometricLoginController.saveRefreshTokenWithBiometric(
                refreshToken = request.refreshToken,
                onSuccess = {
                    viewModel.onBiometricSetupCompleted()
                },
                onError = {
                    // Password login already succeeded.
                    // Continue even if biometric setup was cancelled or failed.
                    viewModel.onBiometricSetupSkipped()
                }
            )
        } else {
            viewModel.onBiometricSetupSkipped()
        }
    }

    LaunchedEffect(navigationRequest) {
        val request = navigationRequest ?: return@LaunchedEffect

        onNavigateToHome()

        viewModel.onNavigationHandled()
    }

    LaunchedEffect(shouldClearStoredBiometricToken) {
        if (shouldClearStoredBiometricToken) {
            biometricLoginController.clearStoredRefreshToken()
            viewModel.onClearStoredBiometricTokenHandled()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "KMP Banking Demo",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = uiState.username,
            onValueChange = viewModel::onUsernameChanged,
            label = {
                Text("Username")
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.password,
            onValueChange = viewModel::onPasswordChanged,
            label = {
                Text("Password")
            },
            visualTransformation = PasswordVisualTransformation(),
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

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = viewModel::onLoginClicked,
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Login")
            }
        }

        if (biometricLoginController.isSupported) {
            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = {
                    biometricLoginController.unlockRefreshTokenWithBiometric(
                        onSuccess = { refreshToken ->
                            viewModel.loginWithRefreshToken(refreshToken)
                        },
                        onError = { message ->
                            viewModel.onBiometricLoginError(message)
                        }
                    )
                },
                enabled = !uiState.isLoading &&
                        biometricLoginController.canUseBiometricLogin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login with Biometrics")
            }
        }
    }
}