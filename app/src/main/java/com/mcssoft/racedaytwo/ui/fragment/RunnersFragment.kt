package com.mcssoft.racedaytwo.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.adapter.runner.IRunnerAdapter
import com.mcssoft.racedaytwo.adapter.runner.RunnerAdapter
import com.mcssoft.racedaytwo.databinding.RunnersFragmentBinding
import com.mcssoft.racedaytwo.entity.cache.RaceCacheEntity
import com.mcssoft.racedaytwo.entity.events.SelectedRunner
import com.mcssoft.racedaytwo.utility.NavManager
import com.mcssoft.racedaytwo.viewmodel.RunnersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Class that displays a list of Runners for a given Race.
 */
@AndroidEntryPoint
class RunnersFragment : Fragment(), View.OnClickListener, IRunnerAdapter {

    @Inject lateinit var runnersViewModel: RunnersViewModel
    @Inject lateinit var navManager: NavManager

    //<editor-fold default state="collapsed" desc="Region: Lifecycle">
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        Log.d("TAG","[RunnersFragment.onCreateView]")
        val binding = RunnersFragmentBinding.inflate(inflater, container, false)
        fragmentBinding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("TAG","[RunnersFragment.onViewCreated]")
        // Nav args contain the Race info.
        runnerArgs = RunnersFragmentArgs.fromBundle(requireArguments())
        // Toolbar title, button listeners, recyclerview etc.
        setUIComponents()
    }

    override fun onStart() {
        super.onStart()
        Log.d("TAG","[RunnersFragment.onStart]")
        collect(true)
    }

    override fun onStop() {
        super.onStop()
        Log.d("TAG","[RunnersFragment.onStop]")
        collect(false)
    }

    override fun onDestroyView() {
        Log.d("TAG","[RunnersFragment.onDestroyView]")
        // Need this explicitly else LeakCanary reports leaks.
        runnerAdapter = null
        fragmentBinding = null
        super.onDestroyView()
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Listener">
    override fun onClick(view: View?) {
        findNavController().navigate(R.id.action_runnersFragment_to_racesFragment)
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Interface">
    override fun onRunnerSelected(selectedRunner: SelectedRunner) {
        // Add in required additional values (used for filtering).
        selectedRunner.raceId = runnerArgs.race.id
        // Update the cache.
        runnersViewModel.setRunnerSelected(selectedRunner)
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Utility">
    /**
     * Initialise UI related components.
     */
    private fun setUIComponents() {
        // Set toolbar title and back nav listener.
        navManager.apply {
            tbView.title = resources.getString(R.string.runners_fragment_name)
            tbView.setNavigationOnClickListener(this@RunnersFragment)
//            disableAllButHome()
        }
        // Set the adapter.
        runnerAdapter = RunnerAdapter(this)
        //
        fragmentBinding?.apply {
            // Set the recyclerview.
            idRunnersRecyclerView.apply {
                /* Note: if setHasFixedSize(true), cause initial display issue where nothing displays
                   but backing data is there. */
                // Add dividers between row items - TBA anything else.
                addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
                // Set the recycler view adapter.
                adapter = runnerAdapter
                // Hide the progress bar when the layout is complete.
                layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            }
        }
        // Set the Race details in the header.
        setHeaderDetail(runnerArgs.race)
    }

    /**
     * Start or stop collect on the flow of Runner detail.
     * @param collect: True, collect the flow, else, cancel the collect job.
     */
    private fun collect(collect: Boolean) {
        if(collect) {
            collectJob = lifecycleScope.launch {
                runnersViewModel.getFromCache(runnerArgs.race.id!!).collect { runners ->
                    runnerAdapter?.submitList(runners)
                }
            }
        } else {
            if(!(collectJob?.isCancelled!!)) {
                collectJob?.cancel()
            }
        }
    }

    /**
     * Set some basic Race info at the top of the Runners listing.
     * @param race: The entity to draw the info from.
     */
    private fun setHeaderDetail(race: RaceCacheEntity) {
        fragmentBinding?.apply {
            idTvMeetingCode.text = race.mtgCode
            idTvRaceNo.text = race.raceNo
            idTvRaceName.text = race.raceName
            idRunnersFragment.setOnClickListener(this@RunnersFragment)
        }
    }
    //</editor-fold>

    private var collectJob: Job? = Job()                         // for collection start/stop etc.
    private var runnerAdapter: RunnerAdapter? = null            // adapter for the recyclerview.
    private lateinit var runnerArgs: RunnersFragmentArgs        // nav args from RacesFragment.
    private var fragmentBinding: RunnersFragmentBinding? = null // for UI components.
}