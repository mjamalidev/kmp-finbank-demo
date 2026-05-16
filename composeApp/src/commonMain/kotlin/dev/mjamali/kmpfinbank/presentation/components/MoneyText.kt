package dev.mjamali.kmpfinbank.presentation.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import dev.mjamali.kmpfinbank.common.util.MoneyFormatter

@Composable
fun MoneyText(
    amountMinor: Long,
    currency: String = "USD",
    visible: Boolean = true
) {
    Text(
        text = MoneyFormatter.format(
            amountMinor = amountMinor,
            currency = currency,
            visible = visible
        )
    )
}