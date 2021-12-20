package com.mcssoft.racedaytwo.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.adapter.race.IRaceAdapter
import com.mcssoft.racedaytwo.adapter.race.RaceAdapter
import com.mcssoft.racedaytwo.databinding.RacesFragmentBinding
import com.mcssoft.racedaytwo.entity.cache.MeetingCacheEntity
import com.mcssoft.racedaytwo.entity.cache.RaceCacheEntity
import com.mcssoft.racedaytwo.repository.RaceDayPreferences
import com.mcssoft.racedaytwo.utility.UIManager
import com.mcssoft.racedaytwo.viewmodel.RacesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Class that implements the list of Races for a particular Meeting.
 * @note Meeting passed as navigation argument.
 */
@AndroidEntryPoint
class RacesFragment : Fragment(), View.OnClickListener, IRaceAdapter {

    @Inject lateinit var racesViewModel: RacesViewModel
    @Inject lateinit var preferences: RaceDayPreferences
    @Inject lateinit var uiManager: UIManager

    //<editor-fold default state="collapsed" desc="Region: Lifecycle">
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        Log.d("TAG","[RacesFragment.onCreateView]")
        val binding = RacesFragmentBinding.inflate(inflater, container, false)
        fragmentBinding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("TAG","[RacesFragment.onViewCreated]")
        // Nav args contain the Meeting code.
        getNavigationArguments()
        // Toolbar title, button listeners, recyclerview etc.
        setUIComponents()
    }

    override fun onStart() {
        super.onStart()
        Log.d("TAG","[RacesFragment.onStart]")
        collect(true)
    }

    override fun onStop() {
        super.onStop()
        Log.d("TAG","[RacesFragment.onStop]")
        collect(false)
    }

    override fun onDestroyView() {
        Log.d("TAG","[RacesFragment.onDestroyView]")
        // Need this explicitly else LeakCanary reports leaks.
        raceAdapter = null
        fragmentBinding = null
        super.onDestroyView()
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Click Listener">
    override fun onClick(view: View) {
        val action = RacesFragmentDirections.actionRacesFragmentToMeetingsFragment()
        findNavController().navigate(action)
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Interface">
    override fun onRaceSelected(race: RaceCacheEntity) {
        // Check if Runners associated with this Race exist in the Runners cache. If not, then
        // create them in the cache (so they can be picked up by the RunnersFragment).
        if(!racesViewModel.checkRunnersInCache(race.id!!)) {
            racesViewModel.populateRunnersCache(race.id!!)
        }
        // Navigate to RunnersFragment.
        val action = RacesFragmentDirections.actionRacesFragmentToRunnersFragment(race)
        findNavController().navigate(action)
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Utility">
    /**
     * Establish the various UI components.
     */
    private fun setUIComponents() {
        // Set toolbar title and back nav listener.
        uiManager.apply {
            tbView.title = resources.getString(R.string.races_fragment_name)
            tbView.setNavigationOnClickListener(this@RacesFragment)
            disableAllButHome()
        }
        // Set the adapter.
        raceAdapter = RaceAdapter(this)
        // Set bindings.
        fragmentBinding?.apply {
            // Set the recyclerview.
            idRacesRecyclerView.apply {
                /* Note: if setHasFixedSize(true), cause initial display issue where nothing displays
                   but backing data is there. */
                // Add dividers between row items - TBA anything else.
                addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
                // Set the recycler view adapter.
                adapter = raceAdapter
                // Hide the progress bar when the layout is complete.
                layoutManager =
                    LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            }
        }
        // Set the Meeting details in the header.
        if (racesArgs.meeting != null) {
            racesArgs.meeting?.let { meeting -> setHeaderDetail(meeting) }
        } else {
            // Back nav from Runner.
            val meeting = racesViewModel.getMeetingFromCache(mtgId)
            setHeaderDetail(meeting)
      }
    }

    /**
     * Start or stop the flow collect.
     * @param collect: True - start the flow collect, else stop.
     */
    private fun collect(collect: Boolean) {
        if(collect) {
            collectJob = lifecycleScope.launch {
                racesViewModel.getRacesByMeetingId(mtgId).collect { races ->
                    raceAdapter?.submitList(races)
                }
            }
        } else {
            if(!(collectJob?.isCancelled!!)) {
                collectJob?.cancel()
            }
        }
    }

    /**
     * Get the arguments passed with the navigation.
     */
    private fun getNavigationArguments() {
        val key = resources.getString(R.string.key_meeting_id)
        racesArgs = RacesFragmentArgs.fromBundle(requireArguments())
        // Save the Meeting id associated with the list of Races. This is primarily for back nav
        // from Runners fragment so the list of Races can be fetched again from the cache.
        when {
            racesArgs.meeting != null -> {
                mtgId = racesArgs.meeting?.id!!
                lifecycleScope.launch(Dispatchers.IO) {
                    preferences.saveMeetingId(key, mtgId)
                }
            }
            else -> {
                // Would be null on back nav from Runners fragment.
                lifecycleScope.launch(Dispatchers.IO) {
                    mtgId = preferences.getMeetingId(key)
                }
                // From testing; needed a delay to make sure local variable is set.
                Thread.sleep(25)
            }
        }
    }

    /**
     * Initialise the values in the meeting details header.
     */
    private fun setHeaderDetail(mce: MeetingCacheEntity) {
        fragmentBinding?.apply {
            idTvMeetingCode.text = mce.meetingCode
            idTvRaceVenueName.text = mce.venueName
            idTvTrackWeather.text = mce.weatherDesc
            idTvTrackDesc.text = mce.trackDesc
            idTvTrackCond.text = mce.trackCond
            idRacesFragment.setOnClickListener(this@RacesFragment)
        }
    }

    //</editor-fold>

    private var collectJob: Job? = Job()                        // for collection start/stop etc.
    private var mtgId: Long = -1                                // nav args or datastore.
    private var raceAdapter: RaceAdapter? = null                // adapter for the recyclerview.
    private lateinit var racesArgs: RacesFragmentArgs           // nav args from MeetingsFragment.
    private var fragmentBinding : RacesFragmentBinding? = null  // for UI components.
}