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
import com.mcssoft.racedaytwo.repository.RaceDayPreferences
import com.mcssoft.racedaytwo.utility.Constants.DOWNLOAD_MAIN_FAILED
import com.mcssoft.racedaytwo.utility.Constants.DOWNLOAD_MAIN_SUCCESS
import com.mcssoft.racedaytwo.utility.Constants.DOWNLOAD_OTHER_FAILED
import com.mcssoft.racedaytwo.utility.Constants.DOWNLOAD_OTHER_SUCCESS
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
    @Inject lateinit var preferences: RaceDayPreferences
    @Inject lateinit var downloader: Downloader
    @Inject lateinit var navManager: NavManager         // simply to remove any navigation options.

    //<editor-fold default state="collapsed" desc="Region: Lifecycle">
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // File download intent filters to signify success or failure.
        downloadFilter = IntentFilter().apply {
            addAction(DOWNLOAD_MAIN_SUCCESS)
            addAction(DOWNLOAD_MAIN_FAILED)
            addAction(DOWNLOAD_OTHER_SUCCESS)
            addAction(DOWNLOAD_OTHER_FAILED)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fragmentBinding = SplashFragmentBinding.inflate(layoutInflater, container, false)
        return fragmentBinding!!.root
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
        super.onStop()
        requireContext().unregisterReceiver(downloadReceiver)
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Listener">
    override fun onClick(view: View) {
        when(view.id) {
            // Retry the downloads again (on some failure).
            R.id.id_btn_retry -> cleanStart()
        }
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Initialise">
    /**
     * Perform file system checks and decide on the "start" type.
     */
    private fun initialise() {
        // Set UI components. This is mainly for a Refresh which causes a programmatic back nav from
        // the MeetingsFragment.
        setViews()
        //
        if (refresh || !downloader.dateCheck(resources.getString(R.string.main_page))) {
            cleanStart()
        } else {
            reStart()
        }
    }

    /**
     * Perform a re-start (just recreate the caches from existing backing data).
     */
    private fun reStart() {
        Log.d("TAG", "[SplashFragment.reStart]")
        fragmentBinding?.apply {
            idProgressBar.visibility = View.VISIBLE
            idBtnRetry.visibility = View.INVISIBLE
            idTvMessage.visibility = View.VISIBLE
        }
        showMessage(resources.getString(R.string.splash_message), "Restarting")
        // Create all caches.
        viewModel.createCaches()
        // Navigate to MeetingsFragment.
        navigateToMeetings()
    }

    /**
     * Perform a "clean" start (basically delete everything, re-download and recreate caches and
     * backing data).
     */
    private fun cleanStart() {
        Log.d("TAG", "[SplashFragment.cleanStart]")
        // Previously changed due to a download failure, then change back.
        fragmentBinding?.apply {
            idProgressBar.visibility = View.VISIBLE
            idBtnRetry.visibility = View.INVISIBLE
            idTvMessage.text = resources.getString(R.string.splash_message)
            idBtnRetry.setOnClickListener(this@SplashFragment)
        }
        // Delete whatever files are there. There could be several dozen or so.
        downloader.clearFileCache()
        // Clear all caches and underlying data.
        viewModel.clearCachesAndData()
        // Get the network (path) url.
        val mainPage = resources.getString(R.string.main_page)
        val url = utilities.createPageUrl(mainPage)
        Log.d("TAG", "URL: $url")
        // Download main page file to parse later.
        lifecycleScope.launch(Dispatchers.IO) {
            downloader.downloadFile(url, mainPage)
        }
        // TBA ? - give time for the download to happen.
        Thread.sleep(100)
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Broadcast receiver">
    // Receiver for the Downloader broadcast.
    private var downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                // Main page download success. Kick off the background processing.
                DOWNLOAD_MAIN_SUCCESS -> execMeetingWorker()
                // Runner page download success.
                DOWNLOAD_OTHER_SUCCESS ->
                    showMessage(resources.getString(R.string.splash_message),
                        "Downloading: ${getIntentMessage(intent, DOWNLOAD_OTHER_SUCCESS)}")
                // Main page download failure.
                DOWNLOAD_MAIN_FAILED -> downloadMainFailed(intent,DOWNLOAD_MAIN_FAILED)
                // Runner page download failure.
                DOWNLOAD_OTHER_FAILED -> downloadOtherFailed(intent, DOWNLOAD_OTHER_FAILED)
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
        showMessage(resources.getString(R.string.splash_message),
            "Generating Meeting and Race info.")
        val workManager = WorkManager.getInstance(requireContext())
        val key = requireContext().resources.getString(R.string.key_file_name)
        val value = requireContext().resources.getString(R.string.main_page)
        val workData = workDataOf(key to value)
        // Create workers. Output of MeetingWorker is input to RunnerWorker.
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
        showMessage(resources.getString(R.string.splash_message), "Generating Runner info.")
        Log.d("TAG","[SplashFragment.execRunnerWorker]")
        val lMtgTypes = arrayOf("R","T","G")
        val key = resources.getString(R.string.key_meeting_type)
        val workManager = WorkManager.getInstance(requireContext())
        val runnerWorker = OneTimeWorkRequestBuilder<RunnerWorker>()
            .setInputData(workDataOf(key to lMtgTypes))
            .addTag("RunnerWorker")
            .build()
        workManager.enqueue(runnerWorker)
        // Observe.
        observeRunnerWorker(workManager, runnerWorker.id)
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: Processing observers">
    private fun observeMeetingWorker(workManager: WorkManager, id: UUID) {
        workManager.getWorkInfoByIdLiveData(id).observe(this) { mtgWorkInfo ->
            when (mtgWorkInfo.state) {
                WorkInfo.State.SUCCEEDED -> {
                    workManager.cancelWorkById(id)
                    // Create Meeting and Race caches. Do in background while processing for Runners.
                    showMessage(resources.getString(R.string.splash_message),
                        "Creating initial caches.")
                    viewModel.createCaches()
                    // Kickoff download and parse for Runner info.
                    execRunnerWorker()
                }
                WorkInfo.State.FAILED -> {
                    Log.d("TAG", "[WorkInfo.State.FAILED] Meeting")
                    workManager.cancelWorkById(id)
                    doMeetingFailure(mtgWorkInfo.outputData)
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
                    doRunnerFailure(workInfo.outputData)
                }
                else -> {}
            }
        }
    }
    //</editor-fold>

    //<editor-fold default state="collapsed" desc="Region: After processing actions">
    private fun doMeetingFailure(data: Data) {
        // TODO - some sort of retry mechanism.
        Log.d("TAG", "[MeetingWorker:WorkInfo.State.Failed]")
        val message = data.getString(resources.getString(R.string.key_result_failure))
        Log.d("TAG", message ?: "No error message available.")

        updateUIBindings(message!!)
    }

    private fun doRunnerFailure(data: Data) {
        // TODO - some sort of retry mechanism.
        Log.d("TAG", "[RunnerWorker:WorkInfo.State.Failed]")
        val message = data.getString(resources.getString(R.string.key_result_failure))
        Log.d("TAG", message ?: "No error message available.")

        updateUIBindings(message!!)
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

    private fun updateUIBindings(message: String) {
        fragmentBinding?.apply {
            idProgressBar.visibility = View.GONE//INVISIBLE
            val retry = StringBuilder().apply {
                append(message)
                appendLine()
                append(resources.getString(R.string.parse_retry_message))
            }.toString()
            idTvMessage.text = retry
            idBtnRetry.visibility = View.VISIBLE
        }
    }

    /**
     * Actions if the main page (RaceDay.xml) download fails.
     * @param intent: The broadcast intent.
     * @param action: The broadcast action.
     */
    private fun downloadMainFailed(intent: Intent, action: String) {
        // Note: Method is called from the broadcast receiver.
        val msg = getIntentMessage(intent, action)
        Log.e("TAG","[SplashFragment.downloadReceiver.onReceive] Main Failure: $msg")

        fragmentBinding?.apply {
            idProgressBar.visibility = View.GONE
            idBtnRetry.visibility = View.VISIBLE
            idTvMessage.visibility = View.VISIBLE
        }
        showMessage("Main page download failure.", msg)
    }

    /**
     * Actions if the other pages respective download fails, e.g. pages "BR.xml" or "SR.xml" etc.
     * @param intent: The broadcast intent.
     * @param action: The broadcast action.
     */
    private fun downloadOtherFailed(intent: Intent, action: String) {
        // Note: Method is called from the broadcast receiver.
        val msg = getIntentMessage(intent, action)
        Log.e("TAG","[SplashFragment.downloadReceiver.onReceive] Other Failure: $msg")
        showMessage("Download failed", msg)
    }

    /**
     * Get the message from the broadcast intent.
     * @param intent: The broadcast intent.
     * @return The message from the intent.
     */
    private fun getIntentMessage(intent: Intent, action: String): String {
        return intent
            .getBundleExtra(action)
            ?.getString(resources.getString(R.string.key_broadcast_message))
            .toString()
    }

    /**
     * Display a message.
     * @param msgTitle: The message title.
     * @param msgText: The text of the message.
     */
    private fun showMessage(msgTitle: String, msgText: String) {
        fragmentBinding?.apply {
            if(idTvMessage.visibility != View.VISIBLE) {
                idTvMessage.visibility = View.VISIBLE
            }
            idTvMessage.text = StringBuilder().apply {
                append(msgTitle)
                appendLine()
                append(msgText)
            }.toString()
        }
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

    private var fragmentBinding: SplashFragmentBinding? = null
    private lateinit var downloadFilter: IntentFilter
    private lateinit var splashArgs: SplashFragmentArgs      // nav args from MeetingsFragment.
    private var refresh: Boolean = false                     // "   "    "    "
}