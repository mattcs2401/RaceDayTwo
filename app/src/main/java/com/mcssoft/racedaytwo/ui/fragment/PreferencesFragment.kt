package com.mcssoft.racedaytwo.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
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
    Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener, View.OnClickListener {
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
        // Set the toolbar title and navigation listener.
        requireActivity().findViewById<Toolbar>(R.id.id_toolbar)?.apply {
            title = resources.getString(R.string.pref_fragment_name)
            setNavigationOnClickListener(this@PreferencesFragment)
        }
    }

//    override fun onStop() {
//        super.onStop()
//        // Simply a marker for testing purposes.
//        Log.d("TAG", "PreferencesFragment.onStop")
//    }

    //<editor-fold default state="collapsed" desc="Region: Listeners">
    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        when(preference.key) {
            resources.getString(R.string.key_use_cache) -> {
                raceDayPreferences.setUseCache(newValue as Boolean)
            }
            resources.getString(R.string.key_default_meeting_type) -> {
                raceDayPreferences.setDefaultMeetingType(newValue as String)
            }
        }
        return true
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        /** Note: OnPreferenceChange will have already happened. **/
        return true
    }

    /**
     * Used for the Up navigation on the toolbar. Seems to be a work around as back arrow displays
     * ok, but nothing would happen when pressed. Navigation config issue ??
     */
    override fun onClick(view: View?) {
        Navigation.findNavController(requireActivity(), R.id.id_nav_host_fragment)
            .navigate(R.id.action_preferencesFragment_to_mainFragment)
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Utility">
    /**
     * Switch preference as whether to re-use existing download file data.
     */
    private fun setUseCache() {
        spCacheUse = SwitchPreferenceCompat(context).apply {
            key = resources.getString(R.string.key_use_cache)
            title = resources.getString(R.string.title_use_cache)
            summary = resources.getString(R.string.summary_use_cache)
        }
        spCacheUse.setDefaultValue(true)
        spCacheUse.onPreferenceChangeListener = this
        screen.addPreference(spCacheUse)
    }

    /**
     * Switch preference to provide setting a default meeting type to act as an initial filter.
     */
    private fun setDefaultMeetingType() {
        msDefaultMeetingType = MultiSelectListPreference(context).apply {
            key = resources.getString(R.string.key_default_meeting_type)
            title = resources.getString(R.string.title_default_meeting_type)
            summary = resources.getString(R.string.summary_default_meeting_type)
            entries = resources.getStringArray(R.array.racing_types)
            entryValues = resources.getStringArray(R.array.racing_values)
        }
        msDefaultMeetingType.setDefaultValue(setOf("R"))
        msDefaultMeetingType.onPreferenceChangeListener = this
        screen.addPreference(msDefaultMeetingType)
    }
    //</editor-fold>

    private lateinit var screen: PreferenceScreen

    private lateinit var spCacheUse: SwitchPreferenceCompat
    private lateinit var msDefaultMeetingType: MultiSelectListPreference
}