package net.wiedekopf.calview.calviewlib.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class CounterPreferences(private val context: Context) {
    fun getCounter(): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            preferences[EXAMPLE_COUNTER] ?: 0
        }
    }

    suspend fun incrementCounter() {
        context.dataStore.edit { settings ->
            val currentCounterValue = settings[EXAMPLE_COUNTER] ?: 0
            settings[EXAMPLE_COUNTER] = currentCounterValue + 1
        }
    }

    companion object {
        private val EXAMPLE_COUNTER = intPreferencesKey("example_counter")
    }
}