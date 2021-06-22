package com.mcssoft.racedaytwo.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.preference.*
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.repository.RaceDayPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
/**
 * Class to implement a "front end" for the app preferences.
 */
class PreferencesFragment : PreferenceFragmentCompat(),
    Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    /*
     Examples:
     https://medium.com/google-developer-experts/exploring-android-jetpack-preferences-8bcb0b7bdd14
     https://blog.mindorks.com/implementing-android-jetpack-preferences
     */
    @Inject lateinit var raceDayPreferences: RaceDayPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // The main preference screen (all preference widgets are a child of / added to, this).
        screen = preferenceManager.createPreferenceScreen(context)
        setUseCache()
        setDefaultMeetingType()
        preferenceScreen = screen
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("TAG", "PreferencesFragment.onViewCreated")
        // Set toolbar title.
        requireActivity().findViewById<Toolbar>(R.id.id_toolbar)?.title =
                resources.getString(R.string.pref_fragment_name)
        initialise()
    }

    override fun onStop() {
        super.onStop()
        // Simply a marker for testing purposes.
        Log.d("TAG", "PreferencesFragment.onStop")
    }

    //<editor-fold default state="collapsed" desc="Region: Listeners">
    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        when(preference.key) {
            "key_use_cache" -> {
                raceDayPreferences.setCacheUse(newValue as Boolean)
            }
            "key_default_meeting_type" -> {
                raceDayPreferences.setDefaultMeetingType(newValue as Boolean)
            }
        }
        return true
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        /** Note: OnPreferenceChange will have already happened. **/
        return true
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Utility">
    private fun initialise() {
        if(spCacheUse.isChecked)
            raceDayPreferences.setCacheUse(true)
        else
            raceDayPreferences.setCacheUse(false)

//        if(msDefaultMeetingType.isChecked)
//            raceDayPreferences.setDefaultMeetingType(true)
//        else
//            raceDayPreferences.setDefaultMeetingType(false)
    }

    /**
     * Switch preference as whether to re-use existing download file data.
     */
    private fun setUseCache() {
        spCacheUse = SwitchPreferenceCompat(context).apply {
            key="key_cache_use"
            title="Use cache."
            setDefaultValue(true)
            summary="Reload application data from cache."
        }
        screen.addPreference(spCacheUse)
    }

    /**
     * Switch preference to provide setting a default meeting type to act as an initial filter.
     */
    private fun setDefaultMeetingType() {
        msDefaultMeetingType = MultiSelectListPreference(context).apply {
            key = "multi_select_list"
            title = "Default Meeting Type"
            summary = "Select the default meeting type/s to display."
            entries = arrayOf("Racing", "Trotting", "Greyhound")
            entryValues = arrayOf("R", "T", "G")
        }
        screen.addPreference(msDefaultMeetingType)
    }
    //</editor-fold>

    private lateinit var screen: PreferenceScreen

    private lateinit var spCacheUse: SwitchPreferenceCompat
    private lateinit var msDefaultMeetingType: MultiSelectListPreference
}