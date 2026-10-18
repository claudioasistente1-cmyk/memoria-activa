package com.claudio.memoria.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

data class AppSettings(
    val reminderEnabled: Boolean = true,
    val reminderHour: Int = 10,
    val reminderMinute: Int = 0,
    val soundEnabled: Boolean = true,
    val lastNotifiedVersionCode: Int = 0,
    val lastUpdateCheckMillis: Long = 0L
)

class SettingsRepository(private val context: Context) {

    private object Keys {
        val reminderEnabled = booleanPreferencesKey("reminder_enabled")
        val reminderHour = intPreferencesKey("reminder_hour")
        val reminderMinute = intPreferencesKey("reminder_minute")
        val soundEnabled = booleanPreferencesKey("sound_enabled")
        val lastNotifiedVersionCode = intPreferencesKey("last_notified_version_code")
        val lastUpdateCheck = longPreferencesKey("last_update_check")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            reminderEnabled = prefs[Keys.reminderEnabled] ?: true,
            reminderHour = prefs[Keys.reminderHour] ?: 10,
            reminderMinute = prefs[Keys.reminderMinute] ?: 0,
            soundEnabled = prefs[Keys.soundEnabled] ?: true,
            lastNotifiedVersionCode = prefs[Keys.lastNotifiedVersionCode] ?: 0,
            lastUpdateCheckMillis = prefs[Keys.lastUpdateCheck] ?: 0L
        )
    }

    suspend fun setReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.reminderEnabled] = enabled }
    }

    suspend fun setReminderTime(hour: Int, minute: Int) {
        context.dataStore.edit {
            it[Keys.reminderHour] = hour.coerceIn(0, 23)
            it[Keys.reminderMinute] = minute.coerceIn(0, 59)
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.soundEnabled] = enabled }
    }

    suspend fun setLastNotifiedVersionCode(code: Int) {
        context.dataStore.edit { it[Keys.lastNotifiedVersionCode] = code }
    }

    suspend fun setLastUpdateCheck(millis: Long) {
        context.dataStore.edit { it[Keys.lastUpdateCheck] = millis }
    }
}
