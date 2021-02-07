package com.mcssoft.racedaytwo.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.databinding.SplashFragmentBinding
import com.mcssoft.racedaytwo.repository.RaceDayPreferences
import com.mcssoft.racedaytwo.repository.RaceDayRepository
import com.mcssoft.racedaytwo.utiliy.RaceDayUtilities
import com.mcssoft.racedaytwo.utiliy.RaceDayWorker
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {

    @Inject lateinit var raceDayUtilities: RaceDayUtilities
    @Inject lateinit var raceDayPreferences: RaceDayPreferences
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
        // Kick it all off.
        initialise()
    }

    override fun onStop() {
        super.onStop()
        Log.d("TAG", "SplashFragment.onStop")
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Utility">
    /**
     * Perform some preferences and file system checks and decide on the "start" type.
     */
    private fun initialise() {
        if(raceDayPreferences.getCacheUse()) {
            if (dateCheck()) {
                // Date is today's date.
                reStart()
            } else {
                // Date isn't today's date.
                cleanStart()
            }
        } else {
            // Preference is not set.
            cleanStart()
        }
    }

    /**
     * Perform a re-start (just recreate the cache).
     */
    private fun reStart() {
        Log.d("TAG", "SplashFragment: Restart")
        // Create repository cache.
        binding.idTvProgress.text = requireContext().getString(R.string.init_cache)
        // create cache if doesn't already exist (and get).
        raceDayRepository.fetchRaceDayList()
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
        Log.d("TAG", "SplashFragment.runRaceDayWorker")
        val url = raceDayUtilities.createRaceDayUrl(requireContext())
        val key_url = requireContext().getString(R.string.key_url)
        val workData = workDataOf(key_url to url)
        val raceDayWorker = OneTimeWorkRequestBuilder<RaceDayWorker>()
                .setInputData(workData)
                .build()

        val workManager = WorkManager.getInstance(requireContext())
        workManager.enqueue(raceDayWorker)
        // Observe.
        workManager.getWorkInfoByIdLiveData(raceDayWorker.id).observe(viewLifecycleOwner) { workInfo ->
//            val data = workInfo.outputData
            when(workInfo.state) {
                WorkInfo.State.SUCCEEDED -> {
//                    raceDayRepository.fetchRaceDayList()
                    navigateToMain()
                }
                WorkInfo.State.FAILED -> {
                    Log.d("TAG", "WorkInfo.State.Failed")
                    workInfo.outputData.getString("key_result_failure")?.let { Log.d("TAG", it) }
                    workInfo.outputData.getString("key_msg")?.let { Log.d("TAG", it) }
                }
                else -> {
                    // TBA.
                }
            }
        }
    }

    /**
     * Programmatic navigation to MainFragment.
     */
    private fun navigateToMain() {
        // Navigate to MainFragment.
        Navigation.findNavController(requireActivity(), R.id.id_nav_host_fragment)
            .navigate(R.id.action_splashFragment_to_mainFragment)
    }

    private fun dateCheck(): Boolean {
        // TBA
        return true
    }
    //</editor-fold>

    private lateinit var binding: SplashFragmentBinding
}