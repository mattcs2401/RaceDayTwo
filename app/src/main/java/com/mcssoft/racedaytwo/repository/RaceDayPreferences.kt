package com.mcssoft.racedaytwo.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.utility.Constants
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * Class to set (proposed) app preferences in a datastore.
 */
class RaceDayPreferences @Inject constructor (private val context: Context) {

    // TODO - Replace the runBlocking() methods with ?? Need something that can return a value.

    private val scope = CoroutineScope(Dispatchers.IO)
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private var dsPreferences: DataStore<Preferences> = context.dataStore

    //<editor-fold default state="collapsed" desc="Region: User selectable preferences">
    fun setUseCache(value: Boolean) = scope.launch {
        saveUseCache(context.resources.getString(R.string.key_use_cache), value)
    }

    fun getUseCache(): Boolean = runBlocking(Dispatchers.IO) {
        return@runBlocking readUseCache(context.resources.getString(R.string.key_use_cache))
    }

    fun setDefaultMeetingType(value: String) = scope.launch {
            saveDefaultType(context.resources.getString(R.string.key_default_meeting_type), value)
    }

    fun getDefaultMeetingType(): String = runBlocking(Dispatchers.IO) {
        readDefaultType(context.resources.getString(R.string.key_default_meeting_type))
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Metadata">
    fun setDownloadId(id: Long) = scope.launch {
        saveFileId(context.resources.getString(R.string.key_download_id), id)
    }

    fun getDownloadId(): Long = runBlocking(Dispatchers.IO) {
        return@runBlocking readFileId(context.resources.getString(R.string.key_download_id))
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Utility - Read/Write preferences">
    /**
     * Save the use cache value to the DataStore.
     * @param key: The key part of a key-value pair.
     * @param value: The value part of a key-value pair.
     */
    private suspend fun saveUseCache(key: String, value: Boolean) {
        val dataStoreKey = booleanPreferencesKey(key)
        dsPreferences.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

    /**
     * Save the downloaded file id value to the DataStore.
     * @param key: The key part of a key-value pair.
     * @param value: The value part of a key-value pair.
     */
    private suspend fun saveFileId(key: String, value: Long) {
        val dataStoreKey = longPreferencesKey(key)
        dsPreferences.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

    /**
     * Save the default Meeting type value to the DataStore.
     * @param key: The key part of a key-value pair.
     * @param value: The value part of a key-value pair.
     */
    private suspend fun saveDefaultType(key: String, value: String) {
        val dataStoreKey = stringPreferencesKey(key)
        dsPreferences.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

    /**
     * Read the use cache value from the DataStore.
     * @param key: The key part of a key-value pair.
     * @return The value based on the key, else false.
     */
    private suspend fun readUseCache(key: String): Boolean {
        val dataStoreKey = booleanPreferencesKey(key)
        val preference = dsPreferences.data.first()
        return preference[dataStoreKey] ?: false
    }

    /**
     * Read the file id value from the DataStore.
     * @param key: The key part of a key-value pair.
     * @return The value based on the key, else -1.
     */
    private suspend fun readFileId(key: String): Long {
        val dataStoreKey = longPreferencesKey(key)
        val preference = dsPreferences.data.first()
        return preference[dataStoreKey] ?: Constants.MINUS_ONE_L
    }

    /**
     * Read the default Meeting type value from the DataStore.
     * @param key: The key part of a key-value pair.
     * @return The value based on the key, else "" (empty string).
     */
    private suspend fun readDefaultType(key: String): String {
        val dataStoreKey = stringPreferencesKey(key)
        val preference = dsPreferences.data.first()
        return preference[dataStoreKey] ?: ""
    }
    //</editor-fold>

}