package dev.mjamali.kmpfinbank

import android.app.Application
import dev.mjamali.kmpfinbank.di.initKoin
import org.koin.android.ext.koin.androidContext

class KmpApp : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@KmpApp)
        }
    }
}