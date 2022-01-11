package com.mcssoft.racedaytwo.utility

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.utility.Constants.DOWNLOAD_MAIN_FAILED
import com.mcssoft.racedaytwo.utility.Constants.DOWNLOAD_MAIN_SUCCESS
import com.mcssoft.racedaytwo.utility.Constants.DOWNLOAD_OTHER_FAILED
import com.mcssoft.racedaytwo.utility.Constants.DOWNLOAD_OTHER_SUCCESS
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
//        .addNetworkInterceptor(ErrorInterceptor())  //
//        .addInterceptor(LogInterceptor())           // testing perhaps ?
//        .connectTimeout(3, TimeUnit.SECONDS)        // this doesn't seem to work for an async conn.
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
                sendErrorBroadcast(urlPage, e.localizedMessage!!)
                Log.e("TAG","Downloader.onFailure(): ${e.localizedMessage}")
            }

            override fun onResponse(call: Call, response: Response) {
                val downloadedFile = File(context.externalCacheDir, urlPage)
                val sink: BufferedSink = downloadedFile.sink().buffer()
                sink.writeAll(response.body!!.source())
                sink.close()
                // Broadcast.
                sendBroadcast(urlPage)
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
     * Send the broadcast.
     * @param page: The download page name.
     */
    private fun sendBroadcast(page: String) {
        // TODO - do we care about success ? or only broadcast on failure ?
        intent = Intent()
        bundle = bundleOf()
        bundle.putString(msgKey, page)

        if(page == mainPage) intent.apply {
            action = DOWNLOAD_MAIN_SUCCESS
            putExtra(DOWNLOAD_MAIN_SUCCESS, bundle)
        } else if (page != mainPage) intent.apply {
            action = DOWNLOAD_OTHER_SUCCESS
            putExtra(DOWNLOAD_OTHER_SUCCESS, bundle)
        }
        context.sendBroadcast(intent)
    }

    /**
     * Send an "error" broadcast.
     * @param page: The download page name.
     * @param message: The error message.
     */
    private fun sendErrorBroadcast(page: String, message: String) {
        intent = Intent()
        bundle = bundleOf()
        bundle.putString(msgKey, message)

        if(page == mainPage) intent.apply {
            action = DOWNLOAD_MAIN_FAILED
            putExtra(DOWNLOAD_MAIN_FAILED, bundle)
        } else if(page != mainPage) intent.apply {
            action = DOWNLOAD_OTHER_FAILED
            putExtra(DOWNLOAD_OTHER_FAILED, bundle)
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