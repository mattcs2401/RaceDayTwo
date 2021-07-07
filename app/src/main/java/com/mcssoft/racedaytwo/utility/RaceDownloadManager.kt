package com.mcssoft.racedaytwo.utility

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.File
import java.io.InputStream
import javax.inject.Inject

/**
 * Simple wrapper for the DownloadManager.
 * @param context: Basically just used to get the Download service.
 */
class RaceDownloadManager @Inject constructor(private val context: Context) {

    private var downloadManager: DownloadManager =
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    /**
     * Basically a simple wrapper for the DownloadManager.enqueue()
     * @param url: The network url to download from.
     * @param path: The path to the directory in the storage.
     * @param fileName: The name of the file.
     */
    fun getPage(url: String, path: String, fileName: String) {
        Log.d("TAG","[RaceDownloadManager.downloadPage]")

        val file = File(path, fileName)

        val dlRequest = DownloadManager.Request(Uri.parse(url))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationUri(Uri.fromFile(file))
            // For Notifications - TBA.
            //.setTitle(context.resources.getString(R.string.raceday_downloading))
            //.setDescription(context.resources.getString(R.string.downloading_details_for_raceday))

        downloadManager.enqueue(dlRequest)
    }

    /**
     * Returns a download as InputStream by the given download identifier
     * @param downloadId: The download id (from the previous download).
     */
    fun getDownloadAsStream(downloadId: Long): InputStream =
        ParcelFileDescriptor.AutoCloseInputStream(downloadManager.openDownloadedFile(downloadId))

//    /**
//     * Returns the cursor associated with the RaceDownloadManager.Query [query].
//     * @Note: Basically only used by the receiver to query the download status.
//     */
//    fun getCursor(query: DownloadManager.Query): Cursor = downloadManager.query(query)

}
/*
  Download file location:
  sdcard/Android/data/...
 */
