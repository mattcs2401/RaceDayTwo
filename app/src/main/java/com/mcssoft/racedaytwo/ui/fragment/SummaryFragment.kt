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
import com.mcssoft.racedaytwo.adapter.summary.SummaryAdapter
import com.mcssoft.racedaytwo.databinding.SummaryFragmentBinding
import com.mcssoft.racedaytwo.utility.UIManager
import com.mcssoft.racedaytwo.viewmodel.SummaryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SummaryFragment: Fragment(), View.OnClickListener {

    @Inject lateinit var summaryViewModel: SummaryViewModel
    @Inject lateinit var uiManager: UIManager

    //<editor-fold default state="collapsed" desc="Region: Lifecycle">
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val binding = SummaryFragmentBinding.inflate(inflater, container, false)
        fragmentBinding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Toolbar title, button listeners, recyclerview etc.
        setUIComponents()
    }

    override fun onStart() {
        super.onStart()
        if(summaryViewModel.getCount() > 0) {
            collect(true)
        } else {
            fragmentBinding?.let { binding ->
                binding.idTvSummaryMessage.visibility = View.VISIBLE
            }
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
        fragmentBinding = null
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
        uiManager.apply {
            tbView.title = resources.getString(R.string.summary_fragment_name)
            tbView.setNavigationOnClickListener(this@SummaryFragment)
            disableAllButHome()
        }
        // Set the adapter.
        summaryAdapter = SummaryAdapter()
        //
        fragmentBinding?.apply {
            // Set the recyclerview.
            idSummaryRecyclerView.apply {
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
//        // Set the Race details in the header.
//        setHeaderDetail(runnerArgs.race)
    }

    /**
     * Start or stop collect on the flow of Runner detail.
     * @param collect: True, collect the flow, else, cancel the collect job.
     */
    private fun collect(collect: Boolean) {
        if(collect) {
            collectJob = lifecycleScope.launch {
                summaryViewModel.getFromCache().collect { summaries ->
                    summaryAdapter?.submitList(summaries)
                }
            }
        } else {
            if(!(collectJob?.isCancelled!!)) {
                collectJob?.cancel()
            }
        }
    }
    //</editor-fold>

    private var collectJob: Job? = Job()                         // for collection start/stop etc.
    private var summaryAdapter: SummaryAdapter? = null            // adapter for the recyclerview.
    private var fragmentBinding : SummaryFragmentBinding? = null    // for UI components.
}