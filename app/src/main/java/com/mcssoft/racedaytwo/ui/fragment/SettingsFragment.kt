package com.mcssoft.racedaytwo.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.databinding.SettingsFragmentBinding
import com.mcssoft.racedaytwo.utility.UIManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment: Fragment(), View.OnClickListener {

    @Inject lateinit var uiManager: UIManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val binding = SettingsFragmentBinding.inflate(inflater, container, false)
        fragmentBinding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("TAG","[SettingsFragment.onViewCreated]")
        // Toolbar title, button listeners, recyclerview etc.
        setUIComponents()
    }

    //<editor-fold default state="collapsed" desc="Region: Click Listener">
    override fun onClick(view: View) {
        val action = SettingsFragmentDirections.actionSettingsFragmentToMeetingsFragment()
        findNavController().navigate(action)
    }
    //</editor-fold>

    /**
     * Establish the various UI components.
     */
    private fun setUIComponents() {
        // Set toolbar title and back nav listener.
        uiManager.apply {
            tbView.title = resources.getString(R.string.settings_fragment_name)
            tbView.setNavigationOnClickListener(this@SettingsFragment)
            disableAllButHome()
        }
    }

    private var fragmentBinding : SettingsFragmentBinding? = null    // for UI components.

}