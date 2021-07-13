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
import com.mcssoft.racedaytwo.utility.*
import com.mcssoft.racedaytwo.viewmodel.RaceDayViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment(), View.OnClickListener {

    @Inject lateinit var raceDayUtilities: RaceDayUtilities
    @Inject lateinit var mainViewModel: RaceDayViewModel
    @Inject lateinit var meetingDownloadManager: MeetingDownloadManager

    //<editor-fold default state="collapsed" desc="Region: Lifecycle">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "[SplashFragment.onCreate]")

        meetingDownloadFilter = IntentFilter().apply {
            addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        }
    // TODO - the very first time the app is run, the datastore won't exist.
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        Log.d("TAG", "[SplashFragment.onCreateView]")
        _binding = SplashFragmentBinding.inflate(inflater, container, false)
        binding.clickListener = this
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        Log.d("TAG", "[SplashFragment.onStart]")
        requireContext().registerReceiver(meetingDownloadReceiver, meetingDownloadFilter)
        // Kick it all off.
        initialise()
    }

    override fun onStop() {
        super.onStop()
        Log.d("TAG", "[SplashFragment.onStop]")
        requireContext().unregisterReceiver(meetingDownloadReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Listener">
    override fun onClick(view: View) {
        when(view.id) {
            R.id.id_btn_refresh -> {
                cleanStart()
            }
        }
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Utility">
    /**
     * Perform some preferences and file system checks and decide on the "start" type.
     */
    private fun initialise() {
//        // The use cache preference is set.
//        if (raceDayUtilities.dateCheck()) {
//            // Date is today's date.
//            reStart()
//        } else {
//            // Date isn't today's date, or date check failed as no file found.
            cleanStart()
//        }
    }

    /**
     * Perform a re-start (just recreate the cache).
     */
    private fun reStart() {
        Log.d("TAG", "[SplashFragment.reStart]")
        binding.idTvProgress.text = resources.getString(R.string.init_cache)
        // Create repository cache.
        mainViewModel.createMeetingsCache()
        // Navigate to MeetingsFragment.
        navigateToMain()
    }

    /**
     * Perform a "clean" start (basically delete everything, re-download and recreate cache and
     * backing data).
     */
    private fun cleanStart() {
        // If was previously made visible from a download failure, then make invisible. If text was
        // previously changed due to a download failure, then change back.
        binding.apply {
            idProgressBar.visibility = View.VISIBLE
            idBtnRefresh.visibility = View.INVISIBLE
            idTvProgress.text = resources.getString(R.string.splash_message)
        }

        Log.d("TAG", "[SplashFragment.cleanStart]")
        val path = raceDayUtilities.getPrimaryStoragePath()
        // Delete whatever file is there.
        raceDayUtilities.deleteFromStorage(File(path))
        // Clear cache and underlying data.
        mainViewModel.clearCacheAndData()
        // Get the network (path) url.
        val url = raceDayUtilities.createRaceDayUrl()
        Log.d("TAG", "URL: $url")
        // Download file to parse later.
        meetingDownloadManager.getPage(url, path, resources.getString(R.string.main_page))
    }

    // Receiver for the DownloadManager broadcast.
    private var meetingDownloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // TODO - download status check, e.g. the download can fail 404, but still broadcast
            //  download completed, i.e. download can be completed but not successful.
            when (intent.action) {
                DownloadManager.ACTION_DOWNLOAD_COMPLETE -> {
                    Log.d("TAG", "[DownloadManager.ACTION_DOWNLOAD_COMPLETE]")
                    when (meetingDownloadManager.getDownloadStatus()) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            Log.d("TAG", "[DownloadManager.STATUS_SUCCESSFUL]")
                            val fileId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, Constants.MINUS_ONE_L)
                            Toast.makeText(context, "Download successful. File id=$fileId", Toast.LENGTH_SHORT).show()
                            parseFileData(context, fileId)
                        }
                        DownloadManager.STATUS_FAILED -> {
                            // TODO - how many times do we try this if it keeps happening.
                            Log.d("TAG", "[DownloadManager.STATUS_FAILED]")
                            //cleanStart()
                            binding.apply {
                                idProgressBar.visibility = View.INVISIBLE
                                idBtnRefresh.visibility = View.VISIBLE
                                idTvProgress.text = context.getString(R.string.retry_message)
                            }
                        } else -> {
                            // TBA. Some other status ?
                        }
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
        // Downloaded file id is input data.
        val workData = workDataOf(context.resources.getString(R.string.key_download_id) to fileId)
        // Create MeetingWorker.
        val meetingWorker = OneTimeWorkRequestBuilder<MeetingWorker>()
            .setInputData(workData)
            .build()
        // Create RaceWorker.
        val raceWorker = OneTimeWorkRequestBuilder<RaceWorker>()
            .setInputData(workData)
            .build()
        // WorkManager instance.
        val workManager = WorkManager.getInstance(requireContext())
        // Chain and enqueue.
        workManager.beginWith(meetingWorker)
            .then(raceWorker)
            .enqueue()
        // Meetings observer.
        observeMeetings(workManager, meetingWorker.id)
        // Races observer.
        observeRaces(workManager, raceWorker.id)
    }

    private fun observeMeetings(workManager: WorkManager, workerId: UUID) {
        workManager.getWorkInfoByIdLiveData(workerId).observe(viewLifecycleOwner) { workInfo ->
            when(workInfo.state) {
                WorkInfo.State.ENQUEUED -> { Log.d("TAG", "[MeetingWorker:WorkInfo.State.Enqueued]") }
                WorkInfo.State.RUNNING -> { Log.d("TAG", "[MeetingWorker:WorkInfo.State.Running]") }
                WorkInfo.State.BLOCKED -> { Log.d("TAG", "[MeetingWorker:WorkInfo.State.Blocked]") }
                WorkInfo.State.CANCELLED -> { Log.d("TAG", "[MeetingWorker:WorkInfo.State.Cancelled]") }
                WorkInfo.State.SUCCEEDED -> {
                    Log.d("TAG", "[MeetingWorker:WorkInfo.State.Succeeded]")
                    mainViewModel.createMeetingsCache()
                    navigateToMain()
                }
                WorkInfo.State.FAILED -> {
                    // TODO - some sort of retry mechanism.
                    Log.d("TAG", "[MeetingWorker:WorkInfo.State.Failed]")
                    val message = workInfo.outputData.getString("key_result_failure")
                    Log.d("TAG", message ?: "No error message available.")
                    workManager.cancelWorkById(workerId)
                    binding.apply {
                        idProgressBar.visibility = View.GONE
                        idTvProgress.text = message
                    }

                }
            }
        }
    }

    private fun observeRaces(workManager: WorkManager, workerId: UUID) {
        workManager.getWorkInfoByIdLiveData(workerId).observe(viewLifecycleOwner) { workInfo ->
            when(workInfo.state) {
                WorkInfo.State.ENQUEUED -> { Log.d("TAG", "[RaceWorker:WorkInfo.State.Enqueued]") }
                WorkInfo.State.RUNNING -> { Log.d("TAG", "[RaceWorker:WorkInfo.State.Running]") }
                WorkInfo.State.BLOCKED -> { Log.d("TAG", "[RaceWorker:WorkInfo.State.Blocked]") }
                WorkInfo.State.CANCELLED -> { Log.d("TAG", "[RaceWorker:WorkInfo.State.Cancelled]") }
                WorkInfo.State.SUCCEEDED -> {
                    Log.d("TAG", "[RaceWorker:WorkInfo.State.Succeeded]")
//                    mainViewModel.createCache()
//                    navigateToMain()
                }
                WorkInfo.State.FAILED -> {
                    // TODO - some sort of retry mechanism.
                    Log.d("TAG", "[RaceWorker:WorkInfo.State.Failed]")
                    val message = workInfo.outputData.getString("key_result_failure")
                    Log.d("TAG", message ?: "No error message available.")
                    workManager.cancelWorkById(workerId)
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

    private var _binding: SplashFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var meetingDownloadFilter: IntentFilter
}