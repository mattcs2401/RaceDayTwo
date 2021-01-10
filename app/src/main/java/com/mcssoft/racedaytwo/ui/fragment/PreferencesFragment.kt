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
//        when(preference.key) {
//            "key_file_use" -> {
//                raceDayPreferences.setFileUse(newValue as Boolean)
//            }
//        }
        return true
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        /** Note: OnPreferenceChange will have already happened. **/
        return true
    }
    //</editor-fold>

//    //<editor-fold default state="collapsed" desc="Region: Utility">
    private fun initialise() {
//        if(spFileUse.isChecked)
//            raceDayPreferences.setFileUse(true)
//        else
//            raceDayPreferences.setFileUse(false)
//
////        raceDayPreferences.setDefaultRaceCodes()
    }
//
//    // Switch preference as whether to re-use existing download file data.
//    private fun setUseFile() {
//        spFileUse = SwitchPreferenceCompat(context).apply {
//            key="key_file_use"
//            title="Use saved file."
//            setDefaultValue(true)
//            summary="Reload application data using saved file."
//        }
//        screen.addPreference(spFileUse)
//    }
//    //</editor-fold>

    private lateinit var screen: PreferenceScreen

    private lateinit var spFileUse: SwitchPreferenceCompat
}