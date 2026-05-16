package dev.mjamali.kmpfinbank.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

actual class AppDataStoreFactory(
    private val context: Context
) {
    actual fun create(): DataStore<Preferences> {
        return PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                context.filesDir
                    .resolve(APP_DATA_STORE_FILE_NAME)
                    .absolutePath
                    .toPath()
            }
        )
    }
}