package dev.maruffirdaus.geopocket.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import dev.maruffirdaus.geopocket.domain.settings.SettingItem
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Singleton

@Singleton
class SettingsRepository(
    private val dataStore: DataStore<Preferences>
) {
    private fun SettingItem.Switch.key(): Preferences.Key<Boolean> = when (this) {
        SettingItem.MeasurementAssist -> booleanPreferencesKey("measurement_assist")
        SettingItem.SmoothInteraction -> booleanPreferencesKey("smooth_interaction")
    }

    suspend fun saveBoolean(item: SettingItem.Switch, value: Boolean) {
        val key = item.key()
        dataStore.updateData {
            it.toMutablePreferences().also { preferences ->
                preferences[key] = value
            }
        }
    }

    suspend fun getBoolean(item: SettingItem.Switch): Boolean {
        val key = item.key()
        return dataStore.data.first()[key] ?: item.default
    }
}