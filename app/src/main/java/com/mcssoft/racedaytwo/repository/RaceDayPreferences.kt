package com.mcssoft.racedaytwo.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Class to set app preferences in a datastore.
 */
class RaceDayPreferences @Inject constructor (context: Context) {

    private val Context.dataStore: DataStore<Preferences>
        by preferencesDataStore(name = "settings")
    private var dsPrefs: DataStore<Preferences> = context.dataStore

    //<editor-fold default state="collapsed" desc="Region: Metadata">
    /**
     * Save a (long) value to the datastore.
     * @param key: The key that is associated with the value.
     * @param value: The value to save.
     */
    suspend fun saveMeetingId(key: String, value: Long) {
        val dsKey = longPreferencesKey(key)
        dsPrefs.edit { prefs ->
            prefs[dsKey] = value
        }
    }

    /**
     * Get a (long) value from the datastore.
     * @param key: The associated key.
     * @return The value, else -1.
     */
    suspend fun getMeetingId(key: String): Long {
        val dsKey = longPreferencesKey(key)
        return dsPrefs.data.first()[dsKey] ?: -1
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: User selectable preferences">
    // TBA.
    //</editor-fold>

}