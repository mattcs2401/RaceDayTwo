package com.mcssoft.racedaytwo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mcssoft.racedaytwo.databinding.SettingsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val binding = SettingsFragmentBinding.inflate(inflater, container, false)
        fragmentBinding = binding
        return binding.root
    }


    private var fragmentBinding : SettingsFragmentBinding? = null    // for UI components.

}