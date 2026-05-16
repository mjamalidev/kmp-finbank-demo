package dev.mjamali.kmpfinbank.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import dev.mjamali.kmpfinbank.common.time.SessionTimer
import dev.mjamali.kmpfinbank.data.cache.DataStoreTransactionCache
import dev.mjamali.kmpfinbank.data.cache.LocalTransactionCache
import dev.mjamali.kmpfinbank.data.mock.BankingMockEngine
import dev.mjamali.kmpfinbank.data.repository.AuthRepositoryImpl
import dev.mjamali.kmpfinbank.data.repository.BankingRepositoryImpl
import dev.mjamali.kmpfinbank.data.repository.ReceiptRepositoryImpl
import dev.mjamali.kmpfinbank.data.repository.SettingsRepositoryImpl
import dev.mjamali.kmpfinbank.domain.repository.AuthRepository
import dev.mjamali.kmpfinbank.domain.repository.BankingRepository
import dev.mjamali.kmpfinbank.domain.repository.ReceiptRepository
import dev.mjamali.kmpfinbank.domain.repository.SettingsRepository
import org.koin.dsl.module

val dataModule = module {

    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = true
            encodeDefaults = true
        }
    }

    single {
        HttpClient(BankingMockEngine()) {
            install(ContentNegotiation) {
                json(get())
            }

            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }
    }

    single<LocalTransactionCache> {
        DataStoreTransactionCache(
            dataStore = get(),
            json = get()
        )
    }

    single<ReceiptRepository> {
        ReceiptRepositoryImpl(
            dataStore = get(),
            json = get()
        )
    }

    single<AuthRepository> {
        AuthRepositoryImpl(
            dataStore = get(),
            httpClient = get()
        )
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(
            dataStore = get()
        )
    }

    single<BankingRepository> {
        BankingRepositoryImpl(
            httpClient = get(),
            transactionCache = get(),
            receiptRepository = get()
        )
    }

    single {
        SessionTimer()
    }
}