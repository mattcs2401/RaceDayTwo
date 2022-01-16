package com.mcssoft.racedaytwo.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mcssoft.racedaytwo.adapter.summary.SummaryAdapter
import com.mcssoft.racedaytwo.databinding.SummaryFragmentBinding
import com.mcssoft.racedaytwo.utility.Constants
import com.mcssoft.racedaytwo.utility.DateUtilities
import com.mcssoft.racedaytwo.viewmodel.SummaryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SummaryFragment: Fragment(), View.OnClickListener {

    @Inject lateinit var summaryViewModel: SummaryViewModel
    @Inject lateinit var dateUtils: DateUtilities

    //<editor-fold default state="collapsed" desc="Region: Lifecycle">
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _fragmentBinding = SummaryFragmentBinding.inflate(inflater, container, false)
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Toolbar title, button listeners, recyclerview etc.
        setUIComponents()
    }

    override fun onStart() {
        super.onStart()
        if(summaryViewModel.getCount() > 0) {
            preCollect()
            Thread.sleep(50) // TBA ?
            collect(true)
        } else {
            fragmentBinding.idTvSummaryMessage.visibility = View.VISIBLE
        }
    }

    override fun onStop() {
        super.onStop()
        collect(false)
    }

    override fun onDestroyView() {
        Log.d("TAG","[RunnersFragment.onDestroyView]")
        // Need this explicitly else LeakCanary reports leaks.
        summaryAdapter = null
        _fragmentBinding = null
        super.onDestroyView()
    }
    //</editor-fold>


    //<editor-fold default state="collapsed" desc="Region: Listener">
    override fun onClick(view: View?) {
        // TBA.
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Utility">
    /**
     * Initialise UI related components.
     */
    private fun setUIComponents() {
        // Set the adapter.
        summaryAdapter = SummaryAdapter()
        //
        fragmentBinding.apply {
            // Set the recyclerview.
            idSummaryRecyclerView.apply {
                ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(this)
                /* Note: if setHasFixedSize(true), cause initial display issue where nothing displays
                   but backing data is there. */
                // Add dividers between row items - TBA anything else.
                addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
                // Set the recycler view adapter.
                adapter = summaryAdapter
                // Hide the progress bar when the layout is complete.
                layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            }
        }
    }

    /**
     * Start or stop collect on the flow of Runner detail.
     * @param collect: True, collect the flow, else, cancel the collect job.
     */
    private fun collect(collect: Boolean) {
        when (collect) {
            true -> {
                collectJob = lifecycleScope.launch {
                    summaryViewModel.getFromSummariesCache().collect { lSummaries ->
                        summaryAdapter?.submitList(lSummaries.sortedBy { it.raceTime })
                    }
                }
            }
            false -> {
                if(!(collectJob?.isCancelled!!)) {
                    collectJob?.cancel()
                }
            }
        }
    }

    /**
     * Update Summaries before they are collected (for display). Basically do the same as the
     * AlarmReceiver, but this happens when the fragment is displayed. This is an attempt to help
     * keep the Summaries as up-to-date as possible.
     */
    private fun preCollect() {
        lifecycleScope.launch {
            summaryViewModel.getFromSummariesCache().collect { lSummary ->
                lSummary.forEach { summary ->
                    if(!summary.elapsed) {
                        val raceTime = dateUtils.timeToMillis(summary.raceTime)
                        when (dateUtils.compareToTime(raceTime)) {
                            Constants.CURRENT_TIME_AFTER -> {
                                summaryViewModel.setElapsed(summary)
                            }
                            Constants.CURRENT_TIME_IN_WINDOW -> {
                                summaryViewModel.setWithinWindow(summary)
                            }
                        }
                    }
                }
            }
        }
    }

    // Swipe to delete implementation.
    private val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback
        = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        // Required but not used ATT.
        override fun onMove(recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder ): Boolean { return false }
        // Actions on swipe.
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val sce = summaryAdapter?.currentList?.get(viewHolder.absoluteAdapterPosition)
            sce?.let { summary -> summaryViewModel.removeSummary(summary) }
            Thread.sleep(50)     // give time to update cache and database.
            if(summaryViewModel.getCount() == 0) {
                fragmentBinding.idTvSummaryMessage.visibility = View.VISIBLE
            } else {
                // Re-start the flow collect to update the adapter's current list.
                collect(false)
                collect(true)
            }
        }
    }
    //</editor-fold>

    private var collectJob: Job? = Job()                        // for collection start/stop etc.
    private var summaryAdapter: SummaryAdapter? = null          // adapter for the recyclerview.

    private var _fragmentBinding : SummaryFragmentBinding? = null    // for UI components.

    private val fragmentBinding : SummaryFragmentBinding
        get() = _fragmentBinding!!
}