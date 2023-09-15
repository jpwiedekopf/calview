package net.wiedekopf.wear.calviewlib.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.wiedekopf.wear.calviewlib.calendars.CalendarItem

private const val PREF_NANE = "settings"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREF_NANE)

class CalendarListRepository(
    private val dataStore: DataStore<Preferences>
) {
    private object PreferencesKeys {
        val pref_json = stringPreferencesKey("pref_json")
    }

    fun getPrefState(): Flow<PrefState> {
        return dataStore.data.map { pref ->
            pref[PreferencesKeys.pref_json]
        }.map { prefJson ->
            if (prefJson == null) {
                val newPrefState = PrefState(emptyMap())
                setPrefState(newPrefState)
                return@map newPrefState
            } else {
                val deserialized = PrefState.fromJson(prefJson)
                    ?: throw NullPointerException("The preference state is null")
                return@map deserialized
            }
        }
    }

    private suspend fun setPrefState(prefState: PrefState) {
        dataStore.edit { pref ->
            val json = prefState.toJson() ?: throw NullPointerException("Could not serialize prefState")
            pref[PreferencesKeys.pref_json] = json
        }
    }
    class PrefState(
        val calendarVisibilityMap: Map<String, Boolean>
    ) {
        val numberOfKnownCalendars get() = calendarVisibilityMap.size
        val calendarNames: List<String> get() = calendarVisibilityMap.keys.toList()

        fun toJson(): String? {
            return moshiAdapter.toJson(this)
        }

        companion object {
            private val moshi: Moshi = Moshi.Builder().build()
            private val moshiAdapter = moshi.adapter(PrefState::class.java)
            fun fromJson(jsonString: String): PrefState? {
                return moshiAdapter.fromJson(jsonString)
            }
        }
    }
}

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