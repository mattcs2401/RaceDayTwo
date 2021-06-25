package com.mcssoft.racedaytwo.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.utility.Constants
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class RaceDayPreferences @Inject constructor (private val context: Context) {

    // TODO - Replace runBlocking() with ??

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private var dsPreferences: DataStore<Preferences> = context.dataStore

    //<editor-fold default state="collapsed" desc="Region: User selectable preferences">
    fun setUseCache(value: Boolean) = runBlocking(Dispatchers.IO) {
        saveBoolean(context.resources.getString(R.string.key_use_cache), value)
    }

    fun getUseCache(): Boolean = runBlocking(Dispatchers.IO) {
        return@runBlocking readBoolean(context.resources.getString(R.string.key_use_cache))
    }

    fun setDefaultMeetingType(value: String) = runBlocking(Dispatchers.IO) {
            saveString(context.resources.getString(R.string.key_default_meeting_type), value)
    }

    fun getDefaultMeetingType(): String = runBlocking(Dispatchers.IO) {
        readString(context.resources.getString(R.string.key_default_meeting_type))
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Metadata">
    fun setDownloadId(id: Long) = runBlocking(Dispatchers.IO) {
        saveLong(context.resources.getString(R.string.key_download_id), id)
    }

//    fun getDownloadId(): Long = runBlocking(Dispatchers.IO) {
//        return@runBlocking readLong(context.resources.getString(R.string.key_download_id))
//    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Utility - Read/Write preferences">
    /**
     * Save the given value to the DataStore.
     * @param key: The key part of a key-value pair.
     * @param value: The value part of a key-value pair.
     */
    private suspend fun saveBoolean(key: String, value: Boolean) {
        val dataStoreKey = booleanPreferencesKey(key)
        dsPreferences.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

    /**
     * Save the given value to the DataStore.
     * @param key: The key part of a key-value pair.
     * @param value: The value part of a key-value pair.
     */
    private suspend fun saveLong(key: String, value: Long) {
        val dataStoreKey = longPreferencesKey(key)
        dsPreferences.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

    /**
     * Save the given value to the DataStore.
     * @param key: The key part of a key-value pair.
     * @param value: The value part of a key-value pair.
     */
    private suspend fun saveString(key: String, value: String) {
        val dataStoreKey = stringPreferencesKey(key)
        dsPreferences.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

    /**
     * Read a value from the DataStore.
     * @param key: The key part of a key-value pair.
     * @return The value based on the key, else false.
     */
    private suspend fun readBoolean(key: String): Boolean {
        val dataStoreKey = booleanPreferencesKey(key)
        val preference = dsPreferences.data.first()
        return preference[dataStoreKey] ?: false
    }

    /**
     * Read a value from the DataStore.
     * @param key: The key part of a key-value pair.
     * @return The value based on the key, else -1.
     */
    private suspend fun readLong(key: String): Long {
        val dataStoreKey = longPreferencesKey(key)
        val preference = dsPreferences.data.first()
        return preference[dataStoreKey] ?: Constants.MINUS_ONE_L
    }

    /**
     * Read a value from the DataStore.
     * @param key: The key part of a key-value pair.
     * @return The value based on the key, else "" (empty string).
     */
    private suspend fun readString(key: String): String {
        val dataStoreKey = stringPreferencesKey(key)
        val preference = dsPreferences.data.first()
        return preference[dataStoreKey] ?: ""
    }
    //</editor-fold>

}