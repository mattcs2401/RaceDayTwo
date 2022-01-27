package com.mcssoft.racedaytwo.ui.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.work.*
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.databinding.SplashFragmentBinding
import com.mcssoft.racedaytwo.utility.Constants.START_TYPE
import com.mcssoft.racedaytwo.utility.Constants.START_TYPE.CLEAN_START
import com.mcssoft.racedaytwo.utility.Constants.START_TYPE.RE_START
import com.mcssoft.racedaytwo.utility.Constants.WORKER_FAILURE
import com.mcssoft.racedaytwo.utility.Constants.WORKER_FAILURE.MEETING
import com.mcssoft.racedaytwo.utility.Constants.WORKER_FAILURE.RUNNER
import com.mcssoft.racedaytwo.utility.Constants.DOWNLOAD_TYPE
import com.mcssoft.racedaytwo.utility.Constants.DOWNLOAD_TYPE.FAILURE_MAIN
import com.mcssoft.racedaytwo.utility.Constants.DOWNLOAD_TYPE.SUCCESS_MAIN
import com.mcssoft.racedaytwo.utility.Constants.DOWNLOAD_TYPE.FAILURE_OTHER
import com.mcssoft.racedaytwo.utility.Constants.DOWNLOAD_TYPE.SUCCESS_OTHER
import com.mcssoft.racedaytwo.utility.DateUtilities
import com.mcssoft.racedaytwo.utility.Downloader
import com.mcssoft.racedaytwo.utility.NavManager
import com.mcssoft.racedaytwo.utility.NavManager.NMView
import com.mcssoft.racedaytwo.viewmodel.SplashViewModel
import com.mcssoft.racedaytwo.worker.MeetingWorker
import com.mcssoft.racedaytwo.worker.RunnerWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * Class that acts as a splash screen whilst downloading and parsing files, then writing the
 * contents to the database, and creating the Meeting and Race caches from the database info.
 */
@AndroidEntryPoint
class SplashFragment : Fragment(), View.OnClickListener {

    @Inject lateinit var utilities: DateUtilities
    @Inject lateinit var viewModel: SplashViewModel
//    @Inject lateinit var preferences: RaceDayPreferences
    @Inject lateinit var downloader: Downloader
    @Inject lateinit var navManager: NavManager         // simply to remove any navigation options.

    //<editor-fold default state="collapsed" desc="Region: Lifecycle">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // File download intent filters to signify success or failure.
        downloadFilter = IntentFilter().apply {
            addAction(SUCCESS_MAIN.toString())
            addAction(FAILURE_MAIN.toString())
            addAction(SUCCESS_OTHER.toString())
            addAction(FAILURE_OTHER.toString())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SplashFragmentBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Nav args for the Refresh menu selection (essentially a back nav from Meetings fragment).
        splashArgs = SplashFragmentArgs.fromBundle(requireArguments())
        refresh = splashArgs.refresh
    }

    override fun onStart() {
        super.onStart()
        // Register the download receiver.
        requireContext().registerReceiver(downloadReceiver, downloadFilter)
        // Kick it all off.
        initialise()
    }

    override fun onStop() {
        requireContext().unregisterReceiver(downloadReceiver)
        super.onStop()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Listener">
    override fun onClick(view: View) {
        when(view.id) {
            // Retry the downloads again (on some failure).
            R.id.id_btn_retry -> start(CLEAN_START)
        }
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Initialise">
    /**
     * Perform file system checks and decide on the "start" type.
     */
    private fun initialise() {
        // Set UI components.
        setViews()
        // Clean start or re-start check.
        if (refresh || !downloader.fileDateCheck(resources.getString(R.string.main_page))) {
            start(CLEAN_START)
        } else {
            start(RE_START)
        }
    }

    /**
     * Where it all begins.
     * @param type: The "startup" type.
     */
    private fun start(type: START_TYPE) {
        updateUIForStart()
        when(type) {
            // Just recreate the caches.
            RE_START -> {
                viewModel.createCaches()
                navigateToMeetings()
            }
            // Delete everything, re-download and create caches.
            CLEAN_START -> {
                binding?.apply {
                    idBtnRetry.setOnClickListener(this@SplashFragment)
                }
                downloader.clearFileCache()
                viewModel.clearCachesAndData()
                // Get the network (path) url.
                val mainPage = resources.getString(R.string.main_page)
                val url = utilities.createPageUrl(mainPage)
                Log.d("TAG", "URL: $url")
                // Download main page file to parse later.
                lifecycleScope.launch(Dispatchers.IO) {
                    downloader.downloadFile(url, mainPage)
                }
                Thread.sleep(100) // TBA ? - give time for the download to happen.
            }
        }
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Broadcast receiver">
    // Receiver for the Downloader broadcast.
    private var downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                // Main page download success. Kick off the background processing.
                SUCCESS_MAIN.toString() -> execMeetingWorker()
                // Runner page download success.
                SUCCESS_OTHER.toString() -> { /* TBA */ }
                // Main page download failure.
                FAILURE_MAIN.toString() -> downloadFailure(FAILURE_MAIN, intent)
                // Runner page download failure.
                FAILURE_OTHER.toString() -> downloadFailure(FAILURE_OTHER, intent)
            }
        }
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Background processing">
    /**
     * Start the Meeting worker, and observe. The Meeting worker takes the previously downloaded
     * main page file and parses the contents into Meeting and associated Race objects in the
     * database. If it returns successfully, the Meeting cache is then created from the database
     * values, and the Runner worker is started.
     */
    private fun execMeetingWorker() {
        val workManager = WorkManager.getInstance(requireContext())
        val key = requireContext().resources.getString(R.string.key_file_name)
        val value = requireContext().resources.getString(R.string.main_page)
        val workData = workDataOf(key to value)
        val meetingWorker = OneTimeWorkRequestBuilder<MeetingWorker>()
            .addTag("MeetingWorker")
            .setInputData(workData)
            .build()
        workManager.enqueue(meetingWorker)
        observeMeetingWorker(workManager, meetingWorker.id)
        Log.d("TAG","[SplashFragment.runMeetingWorker] END")
    }

    /**
     * Start the Runner worker and observe. The Runner worker will download each "runner" file, and
     * parse to create Runner objects in the database and associating them with the existing
     * Meeting/Race objects. If it returns successfully, the Runner cache is created.
     */
    private fun execRunnerWorker() {
        Log.d("TAG","[SplashFragment.execRunnerWorker]")
        val lMtgTypes = arrayOf("R")//,"T","G")
        val key = resources.getString(R.string.key_meeting_type)
        val workManager = WorkManager.getInstance(requireContext())
        val runnerWorker = OneTimeWorkRequestBuilder<RunnerWorker>()
            .setInputData(workDataOf(key to lMtgTypes))
            .addTag("RunnerWorker")
            .build()
        workManager.enqueue(runnerWorker)
        observeRunnerWorker(workManager, runnerWorker.id)
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Processing observers">
    private fun observeMeetingWorker(workManager: WorkManager, id: UUID) {
        workManager.getWorkInfoByIdLiveData(id).observe(this) { workInfo ->
            when (workInfo.state) {
                WorkInfo.State.SUCCEEDED -> {
                    workManager.cancelWorkById(id)
                    viewModel.createCaches()
                    // Kickoff download and parse for Runner info.
                    execRunnerWorker()
                }
                WorkInfo.State.FAILED -> {
                    Log.d("TAG", "[WorkInfo.State.FAILED] Meeting")
                    workManager.cancelWorkById(id)
                    workerFailure(MEETING, workInfo.outputData)
                }
                else -> { }
            }
        }
    }

    private fun observeRunnerWorker(workManager: WorkManager, id: UUID) {
        workManager.getWorkInfoByIdLiveData(id).observe(this) { workInfo ->
            when(workInfo.state) {
                WorkInfo.State.SUCCEEDED -> {
                    workManager.cancelWorkById(id)
                    // Finally :)
                    navigateToMeetings()
                }
                WorkInfo.State.FAILED -> {
                    Log.d("TAG","[WorkInfo.State.FAILED] Runner")
                    workManager.cancelWorkById(id)
                    workerFailure(RUNNER, workInfo.outputData)
                }
                else -> {}
            }
        }
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: After processing actions">
    private fun workerFailure(type: WORKER_FAILURE, data: Data) {
        val message = data.getString(resources.getString(R.string.key_result_failure))

        when(type) {
            MEETING -> {
                Log.d("TAG", "[MeetingWorker:WorkInfo.State.Failed]")
            }
            RUNNER -> {
                Log.d("TAG", "[RunnerWorker:WorkInfo.State.Failed]")
            }
        }
        Log.d("TAG", message ?: "No error message available.")

        updateUIOnFailure()

        binding?.apply {
            idTvMessage.text = StringBuilder().apply {
                append(message)
                appendLine()
                append(resources.getString(R.string.parse_retry_message))
            }.toString()
        }
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Utility">
    /**
     * Programmatic navigation to MeetingsFragment.
     */
    private fun navigateToMeetings() {
        val action = SplashFragmentDirections.actionSplashFragmentToMeetingFragment()
        findNavController().navigate(action)
    }

    private fun downloadFailure(type: DOWNLOAD_TYPE, intent: Intent) {
        val msg = getIntentMessage(intent, type)

        updateUIOnFailure()

        when(type) {
            FAILURE_MAIN -> {
                Log.e("TAG","[SplashFragment.downloadReceiver.onReceive] Main Failure: $msg")
                binding?.apply {
                    idTvMessage.text = resources.getString(R.string.splash_main_fail)}
            }
            FAILURE_OTHER -> {
                Log.e("TAG","[SplashFragment.downloadReceiver.onReceive] Other Failure: $msg")
                binding?.apply {
                    idTvMessage.text = resources.getString(R.string.splash_other_fail)
                }
            }
            else -> {}
        }
    }

    private fun updateUIForStart() {
        binding?.apply {
            idProgressBar.visibility = View.VISIBLE
            idBtnRetry.visibility = View.INVISIBLE
            idTvMessage.visibility = View.VISIBLE
            idTvMessage.text = resources.getString(R.string.splash_message)
        }
    }

    private fun updateUIOnFailure() {
        binding?.apply {
            idProgressBar.visibility = View.GONE
            idBtnRetry.visibility = View.VISIBLE
            idTvMessage.visibility = View.VISIBLE
        }
    }

    /**
     * Get the message from the broadcast intent.
     * @param intent: The broadcast intent.
     * @param type: The download type.
     * @return The message from the intent.
     */
    private fun getIntentMessage(intent: Intent, type: DOWNLOAD_TYPE): String {
        return intent
            .getBundleExtra(type.toString())
            ?.getString(resources.getString(R.string.key_broadcast_message))
            .toString()
    }

    private fun setViews() {
        /** Note: We don't want any navigation options in the SplashFragment. **/
        // If views are visible, then hide them.
        if(navManager.viewVisible(NMView.BOTTOM_NAV_VIEW)) {
            navManager.hideView(NMView.BOTTOM_NAV_VIEW, true)
        }
        if(navManager.viewVisible(NMView.APP_BAR_VIEW)) {
            navManager.hideView(NMView.APP_BAR_VIEW, true)
        }
    }
    //</editor-fold>

    private var binding: SplashFragmentBinding? = null
    private lateinit var downloadFilter: IntentFilter
    private lateinit var splashArgs: SplashFragmentArgs      // nav args from MeetingsFragment.
    private var refresh: Boolean = false                     // "   "    "    "
}