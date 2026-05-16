package dev.mjamali.kmpfinbank.common.util

import kotlin.test.Test
import kotlin.test.assertEquals

class MoneyFormatterTest {

    @Test
    fun formatsMinorUnitsAsUsdAmount() {
        val result = MoneyFormatter.format(
            amountMinor = 245075,
            currency = "USD"
        )

        assertEquals("2,450.75 USD", result)
    }

    @Test
    fun hidesAmountWhenVisibleIsFalse() {
        val result = MoneyFormatter.format(
            amountMinor = 245075,
            currency = "USD",
            visible = false
        )

        assertEquals("••••••••", result)
    }
}