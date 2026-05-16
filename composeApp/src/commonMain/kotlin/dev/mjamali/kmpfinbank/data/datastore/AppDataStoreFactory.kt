package dev.mjamali.kmpfinbank.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

const val APP_DATA_STORE_FILE_NAME = "kmp_fintech.preferences_pb"

expect class AppDataStoreFactory {
    fun create(): DataStore<Preferences>
}