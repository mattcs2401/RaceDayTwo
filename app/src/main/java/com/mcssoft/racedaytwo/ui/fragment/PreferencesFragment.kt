package com.mcssoft.racedaytwo.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import androidx.preference.SwitchPreferenceCompat
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.repository.RaceDayPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PreferencesFragment : PreferenceFragmentCompat(),
    Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    @Inject lateinit var raceDayPreferences: RaceDayPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Only because context is used numerous times.
        val context = requireContext()
        // The main preference screen (all preference widgets are a child of / added to, this).
        screen = preferenceManager.createPreferenceScreen(context)
        setUseCache()
        setDefaultMeetingType()
        setDeleteAll()
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
            "key_cache_use" -> {
                raceDayPreferences.setCacheUse(newValue as Boolean)
            }
            "key_delete_all" -> {
                raceDayPreferences.setDeleteAll(newValue as Boolean)
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

//    //<editor-fold default state="collapsed" desc="Region: Utility">
    private fun initialise() {
        if(spCacheUse.isChecked)
            raceDayPreferences.setCacheUse(true)
        else
            raceDayPreferences.setCacheUse(false)

        if(spDefaultMeetingType.isChecked)
            raceDayPreferences.setDefaultMeetingType(true)
        else
            raceDayPreferences.setDefaultMeetingType(false)

        if(spDeleteAll.isChecked)
            raceDayPreferences.setDeleteAll(true)
        else
            raceDayPreferences.setDeleteAll(false)
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
     * Switch preference to provide a Delete All option on the main toolbar.
     */
    private fun setDeleteAll() {
        spDeleteAll = SwitchPreferenceCompat(context).apply {
            key="key_delete_all"
            title="Delete All."
            setDefaultValue(false)
            summary="Enable a Delete All menu option on the main toolbar."
        }
        screen.addPreference(spDeleteAll)
    }

    /**
     * Switch preference to provide setting a default meeting type to act as an initial filter.
     */
    private fun setDefaultMeetingType() {
        // TODO - this enables a group - TBA.
        spDefaultMeetingType = SwitchPreferenceCompat(context).apply {
            key="key_default_meeting_type"
            title="Default Meeting Type."
            setDefaultValue(false)
            summary="Enable an initial filter for the default meeting type."
        }
        screen.addPreference(spDefaultMeetingType)
    }
    //</editor-fold>

    private lateinit var screen: PreferenceScreen

    private lateinit var spCacheUse: SwitchPreferenceCompat
    private lateinit var spDeleteAll: SwitchPreferenceCompat
    private lateinit var spDefaultMeetingType: SwitchPreferenceCompat
}