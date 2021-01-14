package com.mcssoft.racedaytwo.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.databinding.SplashFragmentBinding
import com.mcssoft.racedaytwo.events.EventResultMessage
import com.mcssoft.racedaytwo.repository.RaceDayRepository
import com.mcssoft.racedaytwo.utiliy.Constants.RESPONSE_RESULT_FAILURE
import com.mcssoft.racedaytwo.utiliy.Constants.RESPONSE_RESULT_SUCCESS
import com.mcssoft.racedaytwo.utiliy.Constants.PARSE_RESULT_FAILURE
import com.mcssoft.racedaytwo.utiliy.Constants.PARSE_RESULT_SUCCESS
import com.mcssoft.racedaytwo.utiliy.RaceDayUtilities
import com.mcssoft.racedaytwo.utiliy.RaceDayWorker
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {

    @Inject lateinit var raceDayUtilities: RaceDayUtilities
//    @Inject lateinit var raceDayPreferences: RaceDayPreferences
    @Inject lateinit var raceDayRepository: RaceDayRepository

    //<editor-fold default state="collapsed" desc="Region: Lifecycle">
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        Log.d("TAG", "SplashFragment.onCreateView")
        return SplashFragmentBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("TAG", "MainFragment.onViewCreated")
        binding = SplashFragmentBinding.bind(view)
    }

    override fun onStart() {
        super.onStart()
        Log.d("TAG", "SplashFragment.onStart")
        // App internal comms.
        EventBus.getDefault().register(this)
        // Kick it all off.
        initialise()
    }

    override fun onStop() {
        super.onStop()
        Log.d("TAG", "SplashFragment.onStop")

        EventBus.getDefault().unregister(this)
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Utility">
    /**
     * Perform some preferences and file system checks and decide on the "start" type.
     */
    private fun initialise() {
        // TODO - add a preference to delete everything and start again.
        // For the time being we'll do that.
        cleanStart()

    }

    /**
     * Act as a communication hub for the results of file download and parsing the xml.
     * @param event: An object that represents the result of an event.
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: EventResultMessage) {
        when(event.result) {
            RESPONSE_RESULT_SUCCESS -> {
                // TBA
            }
            PARSE_RESULT_SUCCESS -> {
                raceDayRepository.createOrRefreshCache()
                navigateToMain()
            }
            RESPONSE_RESULT_FAILURE -> {
                // TODO - some sort of dialog ? with a retry option ?
                Toast.makeText(requireContext(), "Response result failure.", Toast.LENGTH_SHORT).show()
                Log.e("TAG", "Response result failure. Error: ${event.message}")
            }
            PARSE_RESULT_FAILURE -> {
                // TODO - some sort of dialog ? with a retry option ?
                Toast.makeText(requireContext(), "Unable to parse the RESPONSE.", Toast.LENGTH_SHORT).show()
                Log.e("TAG", "Unable to parse the response. Error: ${event.message}")
            }
        }
    }

    /**
     * Perform a re-start (just recreate the cache).
     */
    private fun reStart() {
        Log.d("TAG", "SplashFragment: Restart")
        // Create repository cache.
        binding.idTvProgress.text = requireContext().getString(R.string.init_cache)
        raceDayRepository.createOrRefreshCache()
        // Navigate to MainFragment.
        navigateToMain()
    }

    /**
     * Perform a "clean" start (basically delete everything and recreate).
     */
    private fun cleanStart() {
        Log.d("TAG", "SplashFragment: Clean start")
        // Clear cache and underlying data.
        raceDayRepository.clearCache()
        // Perform the network request, parse and write the response.
        runRaceDayWorker()
    }
    private fun runRaceDayWorker() {
        val url = raceDayUtilities.createRaceDayUrl(requireContext())
        val key_url = requireContext().getString(R.string.key_url)
        val workData = workDataOf(key_url to url)
        val raceDayWorker = OneTimeWorkRequestBuilder<RaceDayWorker>()
                .setInputData(workData)
                .build()
        WorkManager.getInstance(requireContext()).enqueue(raceDayWorker)
    }

    /**
     * Programmatic navigation to MainFragment.
     */
    private fun navigateToMain() {
        // Navigate to MainFragment.
        Navigation.findNavController(requireActivity(), R.id.id_nav_host_fragment)
            .navigate(R.id.action_splashFragment_to_mainFragment)
    }
    //</editor-fold>

    private lateinit var binding: SplashFragmentBinding
}