package dev.mjamali.kmpfinbank

import androidx.compose.ui.window.ComposeUIViewController
import dev.mjamali.kmpfinbank.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
    App()
}