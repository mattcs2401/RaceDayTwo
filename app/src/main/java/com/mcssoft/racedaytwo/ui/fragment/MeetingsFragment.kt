package com.mcssoft.racedaytwo.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButtonToggleGroup
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.adapter.RaceMeetingAdapter
import com.mcssoft.racedaytwo.databinding.MainFragmentBinding
import com.mcssoft.racedaytwo.interfaces.IAdapter
import com.mcssoft.racedaytwo.utility.Constants
import com.mcssoft.racedaytwo.viewmodel.RaceDayViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MeetingsFragment : Fragment(), MaterialButtonToggleGroup.OnButtonCheckedListener, IAdapter {

    @Inject lateinit var mainViewModel: RaceDayViewModel
//    @Inject lateinit var raceDayPreferences: RaceDayPreferences

    //<editor-fold default state="collapsed" desc="Region: Lifecycle">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO - change hard coded defaults ? Get from a datastore ?
        lRaceType = Constants.MEETING_DEFAULT
        mainViewModel.initialise(lRaceType)
    }

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
//        Log.d("TAG","[MeetingsFragment.onCreateView]")
        return MainFragmentBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        Log.d("TAG","[MeetingsFragment.onViewCreated]")
        // Set view binding.
        binding = MainFragmentBinding.bind(view)
        // Set options menu (on toolbar ATT).
        setHasOptionsMenu(true)
        // Set the adapter.
        raceAdapter = RaceMeetingAdapter(this)
    }

    override fun onStart() {
        super.onStart()
        Log.d("TAG","[MeetingsFragment.onStart]")
        // Setup the UI and related components.
        setUIComponents()    // toolbar title, button listeners, recyclerview etc.
        setCollect()         // observe the cache.
    }

    override fun onStop() {
        //super.onDestroy()
        Log.d("TAG","[MeetingsFragment.onStop]")
        binding = null
        collectJob?.cancel()
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        Log.d("TAG","[MeetingsFragment.onCreateOptionsMenu]")
        inflater.inflate(R.menu.options_menu, menu)
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Listeners">
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.id_menu_item_settings -> {
                Toast.makeText(activity, " Settings are TBA ", Toast.LENGTH_SHORT).show()
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onButtonChecked(group: MaterialButtonToggleGroup?, checkedId: Int, isChecked: Boolean) {
        var value = ""  // we will use the button label as the value to add/remove.
        binding?.apply {
            when (checkedId) {
                idBtnRace.id -> {
                    value = idBtnRace.text.toString()
                }
                idBtnTrots.id -> {
                    value = idBtnTrots.text.toString()
                }
                idBtnGreyhound.id -> {
                    value = idBtnGreyhound.text.toString()
                }
            }
            if (isChecked) addToTypeList(value) else removeFromTypeList(value)
        }
        // Cancel the current collect.
        collectJob?.cancel()
        // Update the type filter.
        mainViewModel.setTypeFilter(lRaceType)
        // Restart the collect.
        setCollect()
//        Log.d("TAG", "[Race type: $lRaceType]")
    }

    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: IAdapter">
    override fun onDetailsSelected() {
        Toast.makeText(requireActivity(),"Details is not implemented yet.",Toast.LENGTH_SHORT).show()
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Utility">
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
            idToggleGroup.addOnButtonCheckedListener(this@MeetingsFragment)
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
     * Collect what's coming from the cache.
     */
    private fun setCollect() {
        collectJob = lifecycleScope.launch {
            mainViewModel.getFromCache().collect { meetings ->
                raceAdapter.submitList(meetings)
            }
        }
    }

    /**
     * Add a value to the meeting type filter.
     * @param value: The value to add.
     */
    private fun addToTypeList(value: String) {
        val ndx: Int
        when(value) {
            Constants.MEETING_TYPE_R -> {
                ndx = Constants.R_INDEX
                lRaceType[ndx] = Constants.MEETING_TYPE_R
            }
            Constants.MEETING_TYPE_T -> {
                ndx = Constants.T_INDEX
                lRaceType[ndx] = Constants.MEETING_TYPE_T
            }
            Constants.MEETING_TYPE_G -> {
                ndx = Constants.G_INDEX
                lRaceType[ndx] = Constants.MEETING_TYPE_G
            }
        }
    }

    /**
     * Remove a value from the meeting type filter.
     * @param value: The value to remove. Array element replaced with "".
     */
    private fun removeFromTypeList(value: String) {
        val ndx: Int
        when(value) {
            Constants.MEETING_TYPE_R -> {
                ndx = Constants.R_INDEX
                lRaceType[ndx] = ""
            }
            Constants.MEETING_TYPE_T -> {
                ndx = Constants.T_INDEX
                lRaceType[ndx] = ""
            }
            Constants.MEETING_TYPE_G -> {
                ndx = Constants.G_INDEX
                lRaceType[ndx] = ""
            }
        }
    }
    //</editor-fold>

    private var binding : MainFragmentBinding? = null      // for UI components.
    private var lRaceType = Constants.MEETING_DEFAULT      // meeting type filter default.
    private var collectJob: Job? = null                    // for collection start/stop etc.

    private lateinit var raceAdapter: RaceMeetingAdapter   //

}