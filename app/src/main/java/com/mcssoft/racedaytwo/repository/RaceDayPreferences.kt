package com.mcssoft.racedaytwo.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class RaceDayPreferences @Inject constructor (private val context: Context) {

    // TODO - Replace runBlocking() with ??

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private var dsPreferences: DataStore<Preferences> = context.dataStore
//    private val scope: CoroutineScope = CoroutineScope(context as CoroutineContext)

    //<editor-fold default state="collapsed" desc="Region: User selectable preferences">
    fun setCacheUse(value: Boolean) = runBlocking(Dispatchers.IO) {
        save("key_use_cache", value)
    }

    fun getCacheUse(): Boolean = runBlocking(Dispatchers.IO) {
        return@runBlocking read("key_use_cache")
    }

    fun setDefaultMeetingType(value: Boolean) = runBlocking(Dispatchers.IO) {
            save("key_default_meeting_type", value)
    }
    //</editor-fold>

    /**
     * Save the given value to the DataStore.
     * @param key: The key part of a key-value pair.
     * @param value: The value part of a key-value pair.
     */
    private suspend fun save(key: String, value: Boolean) {
        val dataStoreKey = booleanPreferencesKey(key)
        dsPreferences.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

    /**
     * Read a value from the DataStore.
     * @param key: The key part of a key-value pair.
     * @return The value based on the key, else false.
     */
    private suspend fun read(key: String): Boolean {
        val dataStoreKey = booleanPreferencesKey(key)
        val preference = dsPreferences.data.first()
        return preference[dataStoreKey] ?: false
    }

}