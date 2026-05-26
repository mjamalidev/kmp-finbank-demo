package dev.mjamali.kmpfinbank.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ErrorStateView(
    message: String,
    onRetry: (() -> Unit)? = null
) {
    Column {
        Text(text = message)

        if (onRetry != null) {
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}