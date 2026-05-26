package dev.mjamali.kmpfinbank.di

import dev.mjamali.kmpfinbank.data.datastore.AppDataStoreFactory
import org.koin.dsl.module

actual val platformModule = module {
    single {
        AppDataStoreFactory().create()
    }
}