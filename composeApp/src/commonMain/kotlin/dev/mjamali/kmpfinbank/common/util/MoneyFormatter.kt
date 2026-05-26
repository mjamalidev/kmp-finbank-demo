package dev.mjamali.kmpfinbank.common.util

object MoneyFormatter {

    fun format(
        amountMinor: Long,
        currency: String = "USD",
        visible: Boolean = true
    ): String {
        if (!visible) return "••••••••"

        val sign = if (amountMinor < 0) "-" else ""
        val absoluteAmount = if (amountMinor < 0) -amountMinor else amountMinor

        val major = absoluteAmount / 100
        val minor = absoluteAmount % 100

        val formattedMajor = major
            .toString()
            .reversed()
            .chunked(3)
            .joinToString(",")
            .reversed()

        return "$sign$formattedMajor.${minor.toString().padStart(2, '0')} $currency"
    }

    fun formatInput(
        amountMinor: Long
    ): String {
        val major = amountMinor / 100
        val minor = amountMinor % 100

        return "$major.${minor.toString().padStart(2, '0')}"
    }
}