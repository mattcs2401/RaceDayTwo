package com.mcssoft.racedaytwo.utility

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.utility.Constants.DownloadType.SUCCESS_MAIN
import com.mcssoft.racedaytwo.utility.Constants.DownloadType.SUCCESS_OTHER
import com.mcssoft.racedaytwo.utility.Constants.DownloadType.FAILURE_MAIN
import com.mcssoft.racedaytwo.utility.Constants.DownloadType.FAILURE_OTHER
import com.mcssoft.racedaytwo.utility.Constants.BroadcastType
import com.mcssoft.racedaytwo.utility.Constants.BroadcastType.ERROR
import com.mcssoft.racedaytwo.utility.Constants.BroadcastType.SUCCESS
import okhttp3.*
import okio.BufferedSink
import okio.buffer
import okio.sink
import java.io.*
import javax.inject.Inject

/**
 * A utility class to download the Xml based pages from the network, and save them as files in the
 * local file system. Sends a broadcast representing either success or failure after each download,
 * or on exception.
 */
class Downloader  @Inject constructor(private val context: Context) {
// example: URL("https://tatts.com/pagedata/racing/2022/1/11/RaceDay.xml")

    // Note: Seem to get good results for the downloads when using this.
    private val client: OkHttpClient = OkHttpClient().newBuilder()
//        .retryOnConnectionFailure(true)             // none of this seems to work ?
//        .readTimeout(Duration.ofMillis(3000))       // "    "  "    "     "  "
//        .connectTimeout(Duration.ofMillis(3000))    // "    "  "    "     "  "
        .build()

    private val cachePath
        get() = context.externalCacheDir!!.absolutePath

    // The broadcast intent.
    private lateinit var intent: Intent
    // The bundle associated with the intent.
    private lateinit var bundle: Bundle
    // Main page value.
    private var mainPage: String = context.resources.getString(R.string.main_page)
    // The key of the bundle's message component.
    private var msgKey = context.resources.getString(R.string.key_broadcast_message)

    /**
     * Download the file at the given address to the local external cache.
     * @param url: The Url to download from.
     * @param urlPage: The page name, used for the cache filename.
     */
    fun downloadFile(url: String, urlPage: String) {
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                sendBroadcast(ERROR, urlPage, e.localizedMessage!!)
                Log.e("TAG","Downloader.onFailure(): ${e.localizedMessage}")
            }

            override fun onResponse(call: Call, response: Response) {
                val downloadedFile = File(context.externalCacheDir, urlPage)
                val sink: BufferedSink = downloadedFile.sink().buffer()
                sink.writeAll(response.body!!.source())
                sink.close()
                // Broadcast.
                sendBroadcast(SUCCESS, urlPage)
            }
        })
    }

    /**
     * Get the file as an InputStream.
     * @param fileName: The file name.
     * @return An InputStream.
     */
    fun getFileAsStream(fileName: String): InputStream =
        FileInputStream(File("""${cachePath}${File.separator}$fileName"""))

    /**
     * Delete all the files in the cache.
     */
    fun clearFileCache() = File(cachePath).listFiles()?.forEach { file -> file.delete() }

    /**
     * Method to check if the previously downloaded (RaceDay.xml) file has today's date.
     * @param fileName: The name of the file.
     * @return True:  If the file's last modified date is today, else
     *         False: If the file name doesn't resolve an actual file, or, the file's last modified
     *                date is not today
     */
    fun fileDateCheck(fileName: String): Boolean {
        val path = fileExists(fileName)
        if(path !=  "") {
            return DateUtilities(context).compareDateToToday(File(path).lastModified())
        }
        return false
    }

    /**
     * Send a broadcast back to the receiver in the SplashFragment.
     * @param type: The broadcast type.
     * @param page: The page (file.xml) that was being downloaded.
     * @param message: Optional message.
     */
    private fun sendBroadcast(type: BroadcastType, page: String, message: String = "") {
        // TODO - do we care about success ? or only broadcast on failure ?
        intent = Intent()
        bundle = bundleOf()

        when(type) {
            SUCCESS -> {
                bundle.putString(msgKey, page)
                if(page == mainPage) intent.apply {
                    action = SUCCESS_MAIN.toString()
                    putExtra(action, bundle)
                } else if (page != mainPage) intent.apply {
                    action = SUCCESS_OTHER.toString()
                    putExtra(action, bundle)
                }
            }
            ERROR -> {
                bundle.putString(msgKey, "Page: $page; Message: $message")
                if(page == mainPage) intent.apply {
                    action = FAILURE_MAIN.toString()
                    putExtra(action, bundle)
                } else if(page != mainPage) intent.apply {
                    action = FAILURE_OTHER.toString()
                    putExtra(action, bundle)
                }
            }
        }
        context.sendBroadcast(intent)
    }

    /**
     * Helper method to resolve whether the given filename is actually a file.
     * @param fileName: The name of the file.
     * @return The fully qualified path/filename, else, an empty string, i.e. "".
     */
    private fun fileExists(fileName: String): String {
        val path = """${cachePath}${File.separator}$fileName"""
        val file = File(path)
        return if(file.isFile) { path } else { "" }
    }

}