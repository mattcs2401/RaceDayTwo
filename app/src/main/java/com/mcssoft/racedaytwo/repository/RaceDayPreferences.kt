package com.mcssoft.racedaytwo.repository

import android.content.Context
import javax.inject.Inject

class RaceDayPreferences @Inject constructor (context: Context) {
/* https://developer.android.com/training/data-storage/shared-preferences
*
* Notes:
* "apply() changes the in-memory SharedPreferences object immediately but writes the updates to disk
* asynchronously. Alternatively, you can use commit() to write the data to disk synchronously.
* But because commit() is synchronous, you should avoid calling it from your main thread because
* it could pause your UI rendering."
*/
    private val preferences =
            context.getSharedPreferences("raceday_preferences", Context.MODE_PRIVATE)

    //<editor-fold default state="collapsed" desc="Region: User selectable preferences">
    fun setCacheUse(value: Boolean) {
        with(preferences.edit()) {
            putBoolean("key_cache_use", value)
            apply()
        }
    }

    fun getCacheUse(): Boolean {
        return preferences.getBoolean("key_cache_use", false)
    }

    fun setDefaultMeetingType(value: Boolean) {
        with(preferences.edit()) {
            putBoolean("key_default_meeting_type", value)
            apply()
        }
    }

    fun setDeleteAll(value: Boolean) {
        with(preferences.edit()) {
            putBoolean("key_delete_all", value)
            apply()
        }
    }

    fun getDeleteAll(): Boolean {
        return preferences.getBoolean("key_delete_all", false)
    }
    //</editor-fold>

//    //</editor-fold>

}