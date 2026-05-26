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

    @Test
    fun formatsNegativeMinorUnitsWithSign() {
        val result = MoneyFormatter.format(
            amountMinor = -123456,
            currency = "EUR"
        )

        assertEquals("-1,234.56 EUR", result)
    }

    @Test
    fun formatsInputWithoutCurrency() {
        val result = MoneyFormatter.formatInput(907)

        assertEquals("9.07", result)
    }
}
