package com.mcssoft.racedaytwo.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButtonToggleGroup
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.adapter.RaceMeetingAdapter
import com.mcssoft.racedaytwo.databinding.MainFragmentBinding
import com.mcssoft.racedaytwo.repository.RaceDayPreferences
import com.mcssoft.racedaytwo.utility.Constants
import com.mcssoft.racedaytwo.viewmodel.RaceDayViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment(), MaterialButtonToggleGroup.OnButtonCheckedListener {

    @Inject
    lateinit var mainViewModel: RaceDayViewModel
    @Inject
    lateinit var raceAdapter: RaceMeetingAdapter
    @Inject
    lateinit var raceDayPreferences: RaceDayPreferences

    //<editor-fold default state="collapsed" desc="Region: Lifecycle">
    override fun onAttach(context: Context) {
        super.onAttach(context)
        var backPressedTime: Long = 0
        val backToast = Toast
            .makeText(context, resources.getString(R.string.back_press_text), Toast.LENGTH_SHORT)

        // Setup back press callback. If the back press is selected twice within the time specified,
        // then exit the application. No navigation back to the SplashFragment.
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedTime + Constants.BACK_PRESS_TIME > System.currentTimeMillis()) {
                    backToast.cancel()
                    activity?.finish()
                } else {
                    backToast.show()
                }
                backPressedTime = System.currentTimeMillis()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        Log.d("TAG","[MainFragment.onCreateView]")
        return MainFragmentBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("TAG","[MainFragment.onViewCreated]")
        // set view binding.
        binding = MainFragmentBinding.bind(view)
        // set options menu (on toolbar ATT).
        setHasOptionsMenu(true)
        // Setup the UI and related components.
        initialise()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TAG","[MainFragment.onDestroy]")
        binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        Log.d("TAG","[MainFragment.onCreateOptionsMenu]")
        inflater.inflate(R.menu.options_menu, menu)
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Listeners">
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.id_menu_item_settings -> {
                // Navigate to MainFragment.
                Navigation.findNavController(requireActivity(), R.id.id_nav_host_fragment)
                    .navigate(R.id.action_mainFragment_to_preferencesFragment)
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onButtonChecked(group: MaterialButtonToggleGroup?, checkedId: Int, isChecked: Boolean) {
        when(checkedId) {
            binding?.idBtnRace?.id -> {
                if(isChecked) addToTypeList("R") else removeFromTypeList("R")
            }
            binding?.idBtnTrots?.id -> {
                if(isChecked) addToTypeList("T") else removeFromTypeList("T")
            }
            binding?.idBtnGreyhound?.id -> {
                if(isChecked) addToTypeList("G") else removeFromTypeList("G")
            }
        }
        // Must have one button checked and "R" is it.
        binding?.apply {
            if(!idBtnRace.isChecked && !idBtnTrots.isChecked && !idBtnGreyhound.isChecked) {
                idBtnRace.isChecked = true
                addToTypeList("R")
            }
        }
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Utility">
    private fun initialise() {
        setFromPreferences() // basically the default meeting type.
        setUIComponents()    // toolbar title, button listeners, recyclerview etc.
        setObserve()         // observe the cache.
    }

    /**
     * Get preferences specific to this fragment, basically just the default meeting type (ATT).
     */
    private fun setFromPreferences() {
        // Get default meeting type as per preferences.
        val value = raceDayPreferences.getDefaultMeetingType()
        if(value != "") {
            addToTypeList(value)
        } else {
            addToTypeList(Constants.DEFAULT_MEETING_TYPE)
        }
    }

    /**
     * Establish the various UI components.
      */
    private fun setUIComponents() {
        // Set the title in the toolbar.
        requireActivity().findViewById<Toolbar>(R.id.id_toolbar)?.title =
            resources.getString(R.string.main_fragment_name)
        binding?.apply {
            idBtnRace.isChecked = true
            // Set the toggle group listener.
            idToggleGroup.addOnButtonCheckedListener(this@MainFragment)
            // Set the recyclerview.
            idRecyclerView.apply {
                /* Note: if setHasFixedSize(true), cause initial display issue where nothing displays
                   but backing data is there. */
                // Add dividers between row items - TBA anything else.
                addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
                // Set the recycler view adapter.
                adapter = raceAdapter
                // Hide the progress bar when the layout is complete.
                layoutManager = object : LinearLayoutManager(requireActivity(), VERTICAL, false) {
                    override fun onLayoutCompleted(state: RecyclerView.State?) {
                        super.onLayoutCompleted(state)
                        binding?.idProgressBar?.visibility = View.GONE
                    }
                }
            }
        }
    }

    /**
     * Observe and filter what's coming from the cache.
     */
    private fun setObserve() {
        mainViewModel.raceDayCacheLiveData.observe(viewLifecycleOwner) { meetings ->
            // Filter what's from cache.
            val lMeetings = meetings?.filter { meeting ->
                meeting.meetingType in lRaceType
            }
            raceAdapter.submitList(lMeetings)
        }
    }

    /**
     * Add a value to the meeting type filter.
     * @param value: The value to add.
     */
    private fun addToTypeList(value: String) {
        if (lRaceType.contains(value)) {
            return
        } else {
            lRaceType.add(value)
        }
    }

    /**
     * Remove a value to the meeting type filter.
     * @param value: The value to remove.
     */
    private fun removeFromTypeList(value: String) {
        if(lRaceType.contains(value)) {
            lRaceType.remove(value)
        } else {
            return
        }
    }
    //</editor-fold>

    // For UI components.
    private var binding : MainFragmentBinding? = null
    private var lRaceType = arrayListOf<String>()
}