package com.mcssoft.racedaytwo.ui.fragment

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.IntentFilter
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
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.mcssoft.racedaytwo.utility.*

@AndroidEntryPoint
class SplashFragment : Fragment() {

    @Inject lateinit var raceDayUtilities: RaceDayUtilities
    @Inject lateinit var raceDayPreferences: RaceDayPreferences
    @Inject lateinit var raceDayRepository: RaceDayRepository
    @Inject lateinit var raceDownloadManager: RaceDownloadManager

    //<editor-fold default state="collapsed" desc="Region: Lifecycle">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "[SplashFragment.onCreate]")

        downloadFilter = IntentFilter().apply {
            addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        }

        // Defaults TBA (mainly for app 1st run).
        raceDayPreferences.apply {
            setUseCache(true)
            setDefaultMeetingType("R")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        Log.d("TAG", "[SplashFragment.onCreateView]")
        return SplashFragmentBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("TAG", "[MainFragment.onViewCreated]")
        binding = SplashFragmentBinding.bind(view)
    }

    override fun onStart() {
        super.onStart()
        Log.d("TAG", "[SplashFragment.onStart]")
        requireContext().registerReceiver(raceDownloadReceiver, downloadFilter)
        // Kick it all off.
        initialise()
    }

    override fun onStop() {
        super.onStop()
        Log.d("TAG", "[SplashFragment.onStop]")
        requireContext().unregisterReceiver(raceDownloadReceiver)
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Utility">
    /**
     * Perform some preferences and file system checks and decide on the "start" type.
     */
    private fun initialise() {
        if(raceDayPreferences.getUseCache()) {
            if (raceDayUtilities.dateCheck()) {
                // Date is today's date.
                reStart()
            } else {
                // Date isn't today's date, or date check failed as no file found.
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
        binding.idTvProgress.text = resources.getString(R.string.init_cache)
        // create cache if doesn't already exist (and get).
        raceDayRepository.fetchFromCache()
        // Navigate to MainFragment.
        navigateToMain()
    }

    /**
     * Perform a "clean" start (basically delete everything, re-download and recreate).
     */
    private fun cleanStart() {
        Log.d("TAG", "SplashFragment: Clean start")
        val path = raceDayUtilities.getPrimaryStoragePath()
        // Delete whatever file is there.
        raceDayUtilities.deleteFromStorage(File(path))
        // Clear cache and underlying data.
        raceDayRepository.clearCache()
        // Get the network (path) url.
        val url = raceDayUtilities.createRaceDayUrl(requireContext())
        // Download file to parse later.
        raceDownloadManager.getPage(url, path, "RaceDay.xml")
    }

    // receiver for the DownloadManager broadcast.
    private var raceDownloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action) {
                DownloadManager.ACTION_DOWNLOAD_COMPLETE -> {
                    Log.d("TAG","DownloadManager.ACTION_DOWNLOAD_COMPLETE")

                    // Get the file id of the downloaded file.
                    val fileId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,
                        Constants.MINUS_ONE_L)

                    if(fileId != Constants.MINUS_ONE_L) {
                        // Download was successful (ATT testing ?).
                        Toast.makeText(context, "Download successful. File id=$fileId", Toast.LENGTH_SHORT).show()
                        // Save file id to preferences as metadata.
                        raceDayPreferences.setDownloadId(fileId)
                        // Parse the file data.
                        parseFileData(context, fileId)
                    } else {
                        // TODO - some sort of retry strategy based on the DownloadManager COLUMN_REASON
                        //  and COLUMN_STATUS codes.
                        // Download was not successful.
                        Toast.makeText(context, "Download not successful.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    /**
     * Hand off to WorkManager to parse the downloaded file.
     * @param context: Used for Resources and WorkManager instance.
     * @param fileId: Id of the downloaded file.
     */
    private fun parseFileData(context: Context, fileId: Long) {
        val workData = workDataOf(context.resources.getString(R.string.key_download_id) to fileId)
        val raceDayWorker = OneTimeWorkRequestBuilder<RaceDayWorker>()
            .setInputData(workData).
            build()
        val workManager = WorkManager.getInstance(requireContext())
        workManager.enqueue(raceDayWorker)

        // Observe.
        workManager.getWorkInfoByIdLiveData(raceDayWorker.id).observe(viewLifecycleOwner) { workInfo ->
            when(workInfo.state) {
                WorkInfo.State.ENQUEUED -> { Log.d("TAG", "WorkInfo.State.Enqueued") }
                WorkInfo.State.RUNNING -> { Log.d("TAG", "WorkInfo.State.Running") }
                WorkInfo.State.BLOCKED -> { Log.d("TAG", "WorkInfo.State.Blocked") }
                WorkInfo.State.CANCELLED -> { Log.d("TAG", "WorkInfo.State.Cancelled") }
                WorkInfo.State.SUCCEEDED -> {
                    Log.d("TAG", "WorkInfo.State.Succeeded")
                    navigateToMain()
                }
                WorkInfo.State.FAILED -> {
                    Log.d("TAG", "WorkInfo.State.Failed")
                    workInfo.outputData.getString("key_result_failure")?.let { Log.d("TAG", it) }
                    workInfo.outputData.getString("key_msg")?.let { Log.d("TAG", it) }
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
    //</editor-fold>

    private lateinit var binding: SplashFragmentBinding
    private lateinit var downloadFilter: IntentFilter
}