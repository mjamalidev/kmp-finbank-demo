package dev.mjamali.kmpfinbank

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import dev.mjamali.kmpfinbank.presentation.navigation.AppNavHost

@Composable
fun App() {
    MaterialTheme {
        AppNavHost()
    }
}