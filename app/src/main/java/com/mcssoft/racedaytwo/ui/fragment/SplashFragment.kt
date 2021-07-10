package com.mcssoft.racedaytwo.ui.fragment

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.databinding.SplashFragmentBinding
import com.mcssoft.racedaytwo.utility.Constants
import com.mcssoft.racedaytwo.utility.RaceDayUtilities
import com.mcssoft.racedaytwo.utility.RaceDayWorker
import com.mcssoft.racedaytwo.utility.MeetingDownloadManager
import com.mcssoft.racedaytwo.viewmodel.RaceDayViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {

    @Inject lateinit var raceDayUtilities: RaceDayUtilities
    @Inject lateinit var mainViewModel: RaceDayViewModel
    @Inject lateinit var meetingDownloadManager: MeetingDownloadManager

    //<editor-fold default state="collapsed" desc="Region: Lifecycle">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "[SplashFragment.onCreate]")

        downloadFilter = IntentFilter().apply {
            addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        }

        // TODO - the very first time the app is run, the datastore won't exist.
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        Log.d("TAG", "[SplashFragment.onCreateView]")
        return SplashFragmentBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("TAG", "[SplashFragment.onViewCreated]")
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
        // The use cache preference is set.
        if (raceDayUtilities.dateCheck()) {
            // Date is today's date.
            reStart()
        } else {
            // Date isn't today's date, or date check failed as no file found.
            cleanStart()
        }
    }

    /**
     * Perform a re-start (just recreate the cache).
     */
    private fun reStart() {
        Log.d("TAG", "[SplashFragment.reStart]")
        binding.idTvProgress.text = resources.getString(R.string.init_cache)
        // Create repository cache.
        mainViewModel.createCache()
        // Navigate to MeetingsFragment.
        navigateToMain()
    }

    /**
     * Perform a "clean" start (basically delete everything, re-download and recreate).
     */
    private fun cleanStart() {
        Log.d("TAG", "[SplashFragment.cleanStart]")
        val path = raceDayUtilities.getPrimaryStoragePath()
        // Delete whatever file is there.
        raceDayUtilities.deleteFromStorage(File(path))
        // Clear cache and underlying data.
        mainViewModel.clearCacheAndData()
        // Get the network (path) url.
        val url = raceDayUtilities.createRaceDayUrl(requireContext())
        // Download file to parse later.
        meetingDownloadManager.getPage(url, path, resources.getString(R.string.main_page))
    }

    // Receiver for the DownloadManager broadcast.
    private var raceDownloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action) {
                DownloadManager.ACTION_DOWNLOAD_COMPLETE -> {
                    Log.d("TAG","[DownloadManager.ACTION_DOWNLOAD_COMPLETE]")

                    // Get the file id of the downloaded file.
                    val fileId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,
                        Constants.MINUS_ONE_L)

                    if(fileId != Constants.MINUS_ONE_L) {
                        // Download was successful (ATT testing ?).
                        Toast.makeText(context, "Download successful. File id=$fileId", Toast.LENGTH_SHORT).show()
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
                WorkInfo.State.ENQUEUED -> { Log.d("TAG", "[WorkInfo.State.Enqueued]") }
                WorkInfo.State.RUNNING -> { Log.d("TAG", "[WorkInfo.State.Running]") }
                WorkInfo.State.BLOCKED -> { Log.d("TAG", "[WorkInfo.State.Blocked]") }
                WorkInfo.State.CANCELLED -> { Log.d("TAG", "[WorkInfo.State.Cancelled]") }
                WorkInfo.State.SUCCEEDED -> {
                    Log.d("TAG", "[WorkInfo.State.Succeeded]")
                    mainViewModel.createCache()
                    navigateToMain()
                }
                WorkInfo.State.FAILED -> {
                    // TODO - some sort of retry mechanism, maybe through a dialog. At least need to
                    //        notify somehow, not just continually sit there.
                    Log.d("TAG", "[WorkInfo.State.Failed]")
                    // TBA.
                    workInfo.outputData.getString("key_result_failure")?.let { Log.d("TAG", it) }
                    workInfo.outputData.getString("key_msg")?.let { Log.d("TAG", it) }

                    workManager.cancelWorkById(raceDayWorker.id)
                }
            }
        }
    }

    /**
     * Programmatic navigation to MeetingsFragment.
     */
    private fun navigateToMain() {
        // Navigate to MeetingsFragment.
        Navigation.findNavController(requireActivity(), R.id.id_nav_host_fragment)
            .navigate(R.id.action_splashFragment_to_mainFragment)
    }
    //</editor-fold>

    private lateinit var binding: SplashFragmentBinding
    private lateinit var downloadFilter: IntentFilter
}