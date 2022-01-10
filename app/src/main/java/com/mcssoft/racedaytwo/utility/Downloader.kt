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
// example: URL("https://tatts.com/pagedata/racing/2022/1/10/RaceDay.xml")

    private val client: OkHttpClient = OkHttpClient().newBuilder()
        .addNetworkInterceptor(ErrorInterceptor())
        .build()

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
     * @param urlPage: The page name, used for the filename.
     */
    fun downloadFile(url: String, urlPage: String) {
        val request = createRequest(url)    // the Request object.

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // TODO - some sort of error message to the user.
                Log.e("TAG","Downloader.onFailure(): ${e.localizedMessage}")
            }

            @Throws(IOException::class)
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
     * create a request for the given url.
     * @param url: the Url e.g. "http://some/site/somewhere
     */
    private fun createRequest(url: String): Request = Request.Builder().url(url).build()

    /**
     * Get the file as an InputStream.
     * @param fileName: The file name.
     * @return An InputStream.
     */

    fun getFileAsStream(fileName: String): InputStream {
        val path = """${getCachePath()}${File.separator}$fileName"""
        return FileInputStream(File(path))
    }

    /**
     * Delete all the files in the cache.
     */
    fun clearFileCache() {
        File(getCachePath()).listFiles()?.forEach { file -> file.delete() }
    }

    /**
     * Method to check if the previously downloaded RaceDay.xml file has today's date.
     * @param fileName: The name of the file.
     * @return True:  If the file's last modified date is today, else
     *         False: If the file name doesn't resolve an actual file, or, the file's last modified
     *                date is not today
     */
    fun dateCheck(fileName: String): Boolean {
        val path = fileExists(fileName)
        if(path !=  "") {
            return DateUtilities(context).compareDateToToday(File(path).lastModified())
        }
        return false
    }

    /**
     * Get the fully qualified path to the file cache.
     * @return The path.
     */
    private fun getCachePath(): String {
        return context.externalCacheDir!!.absolutePath
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
        val path = """${getCachePath()}${File.separator}$fileName"""
        val file = File(path)
        return if(file.isFile) { path } else { "" }
    }

    private fun handleErrors(ex: Exception, urlPath: String, urlPage: String) {
        val msg = when (ex) {
            is FileNotFoundException -> {
                setMessage("File not found: $urlPage", urlPath)
            }
            is IOException -> {
                setMessage("Network error: $urlPage", urlPath)
            }
            else -> {
                setMessage("An error occurred: $urlPage", ex.message.toString())
            }
        }
        sendErrorBroadcast(urlPage, msg)
    }

    /**
     * Construct a message from given parts and return.
     * @param msgTitle: The message title.
     * @param msgText: The message text.
     * @param msgText2: Optional message line.
     * @return The given message text separated by CR/LF.
     */
    private fun setMessage(msgTitle: String, msgText: String, msgText2: String = ""): String {
        return StringBuilder().apply {
            append(msgTitle)
            appendLine()
            append(msgText)
            if(msgText2 != "") {
                appendLine()
                append(msgText2)
            }
        }.toString()
    }
}