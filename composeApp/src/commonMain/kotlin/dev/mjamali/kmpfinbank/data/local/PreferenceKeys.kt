package dev.mjamali.kmpfinbank.data.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val BALANCE_VISIBLE = booleanPreferencesKey("balance_visible")
    val SESSION_TOKEN = stringPreferencesKey("session_token")
    val LAST_ACTIVE_AT = longPreferencesKey("last_active_at")
    val TRANSACTIONS_CACHE_JSON = stringPreferencesKey("transactions_cache_json")
    val LAST_RECEIPT_JSON = stringPreferencesKey("last_receipt_json")
}