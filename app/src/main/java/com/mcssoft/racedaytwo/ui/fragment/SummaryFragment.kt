package com.mcssoft.racedaytwo.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.adapter.summary.SummaryAdapter
import com.mcssoft.racedaytwo.databinding.SummaryFragmentBinding
import com.mcssoft.racedaytwo.utility.NavManager
import com.mcssoft.racedaytwo.viewmodel.SummaryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SummaryFragment: Fragment(), View.OnClickListener {

    @Inject lateinit var summaryViewModel: SummaryViewModel
    @Inject lateinit var navManager: NavManager

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

        collectLatestFlow(summaryViewModel.getCountAsFlow()) { count ->
            if(count > 0) {
                collect(true)
            } else {
                fragmentBinding.idTvSummaryMessage.visibility = View.VISIBLE
            }
        }
    }

    override fun onStop() {
        collect(false)
        super.onStop()
    }

    override fun onDestroyView() {
        // Need this explicitly else LeakCanary reports leaks.
        summaryAdapter = null
        _fragmentBinding = null
        super.onDestroyView()
    }
    //</editor-fold>


    //<editor-fold default state="collapsed" desc="Region: Listener">
    override fun onClick(view: View?) {
        findNavController().navigate(R.id.action_summaryFragment_to_meetingsFragment)
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Utility">
    /**
     * Initialise UI related components.
     */
    private fun setUIComponents() {
        // Set title and back nav listener.
        navManager.apply {
            tbView.title = resources.getString(R.string.summary_fragment_name)
            tbView.setNavigationOnClickListener(this@SummaryFragment)
            disableAllButHome()
        }
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
        if(collect) {
            collectJob = lifecycleScope.launch {
                summaryViewModel.getFromCache().collect { summaries ->
                    summaryAdapter?.submitList(summaries.sortedBy { it.raceTime })
                }
            }
        } else {
            if(!(collectJob?.isCancelled!!)) {
                collectJob?.cancel()
            }
        }
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: ItemTouchHelper">
    // Swipe to delete implementation.
    private val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback
        = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        // Required but not used ATT.
        override fun onMove(rcv: RecyclerView, vh: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder): Boolean { return false }

        //
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val sce = summaryAdapter?.currentList?.get(viewHolder.absoluteAdapterPosition)
            sce?.let { summary -> summaryViewModel.removeSummary(summary) }
            Thread.sleep(50)     // give time to update cache and database.

            collectLatestFlow(summaryViewModel.getCountAsFlow()) { count ->
                if (count == 0) {
                    fragmentBinding.idTvSummaryMessage.visibility = View.VISIBLE
                } else {
                    // Re-start the flow collect to update the adapter's current list.
                    collect(false)
                    collect(true)
                }
            }
        }
    }
    //</editor-fold>

    private var collectJob: Job? = Job()                         // for collection start/stop etc.
    private var summaryAdapter: SummaryAdapter? = null            // adapter for the recyclerview.
    private var _fragmentBinding : SummaryFragmentBinding? = null    // for UI components.
    private val fragmentBinding : SummaryFragmentBinding
        get() = _fragmentBinding!!
}

fun <T> Fragment.collectLatestFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(STARTED) {
            flow.collectLatest(collect)
        }
    }
}

//fun <T> Fragment.collectFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
//    lifecycleScope.launch {
//        repeatOnLifecycle(STARTED) {
//            flow.collect(collect)
//        }
//    }
//}