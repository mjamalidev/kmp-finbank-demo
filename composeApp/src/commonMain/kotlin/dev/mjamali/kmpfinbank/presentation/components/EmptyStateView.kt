package dev.mjamali.kmpfinbank.presentation.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun EmptyStateView(
    message: String = "No data available."
) {
    Text(text = message)
}