package com.mcssoft.racedaytwo.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.adapter.meeting.IMeetingAdapter
import com.mcssoft.racedaytwo.adapter.meeting.MeetingAdapter
import com.mcssoft.racedaytwo.databinding.MeetingsFragmentBinding
import com.mcssoft.racedaytwo.entity.cache.MeetingCacheEntity
import com.mcssoft.racedaytwo.repository.RaceDayPreferences
import com.mcssoft.racedaytwo.utility.Constants
import com.mcssoft.racedaytwo.viewmodel.MeetingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.mcssoft.racedaytwo.utility.UIManager
import com.mcssoft.racedaytwo.utility.UIManager.UIMgr

/**
 * Class that implements the list of Meetings.
 */
@AndroidEntryPoint
class MeetingsFragment : Fragment(), IMeetingAdapter {

    @Inject lateinit var mainViewModel: MeetingsViewModel
    @Inject lateinit var preferences: RaceDayPreferences
    @Inject lateinit var  uiManager: UIManager

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
        super.onCreateView(inflater, container, savedInstanceState)
        val binding = MeetingsFragmentBinding.inflate(inflater, container, false)
        fragmentBinding = binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Setup the UI and related components.
        setUIComponents()
    }

    override fun onStart() {
        super.onStart()
        collect(true)  // observe the cache.
    }

    override fun onStop() {
        super.onStop()
        collect(false)
    }

    override fun onDestroyView() {
        Log.d("TAG", "[MeetingsFragment.onDestroyView]")
        // Need this explicitly else LeakCanary reports leaks.
        meetingAdapter = null
        fragmentBinding = null
        super.onDestroyView()
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: IMeetingAdapter">
    /**
     * Provide the navigation to the RacesFragment.
     * @param meeting: The Meeting for the navigation args.
     */
    override fun onDetailsSelected(meeting: MeetingCacheEntity) {
        // Set the navigation args (the selected Meeting) and navigate to RacesFragment.
        // Note: alternate navigate(R.id.action_a_to_b) doesn't take a navigation argument.
        val action = MeetingsFragmentDirections.actionMeetingFragmentToRaceFragment(meeting)
        findNavController().navigate(action)
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Utility">
    /**
     * Establish the various UI components.
      */
    private fun setUIComponents() {
        uiManager.apply {
            hideView(UIMgr.BOTTOM_NAV_VIEW, false)
            hideView(UIMgr.APP_BAR_VIEW, false)
            tbView.title = resources.getString(R.string.meeting_fragment_name)
            enableHome(false)
            enableSummary(true)
            enableSettings(true)
            enableRefresh(true)
        }
        // Set the adapter.
        meetingAdapter = MeetingAdapter(this)
        // Other bindings.
        fragmentBinding?.apply {
            // Set the recyclerview.
            idRecyclerView.apply {
                // Add dividers between row items - TBA anything else.
                addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
                // Set the recycler view adapter.
                adapter = meetingAdapter
                // Set the layout manager.
                layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            }
        }
    }

    /**
     * Start or stop the flow collect.
     * @param collect: True - start the flow collect, else stop.
     */
    private fun collect(collect: Boolean) {
        if(collect) {
            collectJob = lifecycleScope.launch {
                mainViewModel.getMeetingsFromCache().collect { meetings ->
                    meetingAdapter?.submitList(meetings)
                }
            }
        } else {
            if(!(collectJob?.isCancelled!!)) {
                collectJob?.cancel()
            }
        }
    }

    //</editor-fold>

    private var collectJob: Job? = Job()                              // for collection start/stop.
    private var meetingAdapter: MeetingAdapter? = null               // adapter for recycler view.
    private var fragmentBinding: MeetingsFragmentBinding? = null     // for UI components.

}