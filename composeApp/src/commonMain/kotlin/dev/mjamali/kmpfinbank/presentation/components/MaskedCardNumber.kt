package dev.mjamali.kmpfinbank.presentation.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun MaskedCardNumber(
    cardNumber: String
) {
    Text(text = "**** **** **** ${cardNumber.takeLast(4)}")
}