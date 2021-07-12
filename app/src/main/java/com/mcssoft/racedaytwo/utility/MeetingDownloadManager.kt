package com.mcssoft.racedaytwo.utility

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.InputStream
import javax.inject.Inject

/**
 * Simple wrapper for the DownloadManager.
 * @param context: Basically just used to get the Download service.
 */
class MeetingDownloadManager @Inject constructor(private val context: Context) {

    private var downloadManager: DownloadManager =
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    private var enqueueId: Long = Constants.MINUS_ONE_L

    /**
     * Basically a simple wrapper for the DownloadManager.enqueue()
     * @param url: The network url to download from.
     * @param path: The path to the directory in the storage.
     * @param fileName: The name of the file.
     */
    fun getPage(url: String, path: String, fileName: String) {
        Log.d("TAG","[MeetingDownloadManager.getPage]")

        val file = File(path, fileName)

        val dlRequest = DownloadManager.Request(Uri.parse(url))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationUri(Uri.fromFile(file))
            // For Notifications - TBA.
            //.setTitle(context.resources.getString(R.string.raceday_downloading))
            //.setDescription(context.resources.getString(R.string.downloading_details_for_raceday))

        enqueueId = downloadManager.enqueue(dlRequest)
    }

    /**
     * Returns a download as InputStream by the given download identifier
     * @param downloadId: The download id (from the previous download).
     */
    fun getDownloadAsStream(downloadId: Long): InputStream =
        ParcelFileDescriptor.AutoCloseInputStream(downloadManager.openDownloadedFile(downloadId))

    /**
     * Get the id returned by the enqueue() operation.
     */
    fun getEnqueueId() = enqueueId

    /**
     * Returns the cursor associated with the MeetingDownloadManager.Query [query].
     * @param dmQuery: The Query.
     * @Note: Basically only used by the receiver to query the download status.
     */
    fun getCursor(dmQuery: DownloadManager.Query): Cursor = downloadManager.query(dmQuery)

    /**
     * Get the status of the download.
     * @return An array of values. TBA
     */
    fun getDownloadStatus(): Int {
        val query = DownloadManager.Query()
        query.setFilterById(getEnqueueId())
        val cursor = getCursor(query)

        if(cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val status = cursor.getInt(columnIndex)
            return status
        } else {
            return Constants.MINUS_ONE_L.toInt()
        }
    }

}
/*
  Download file location:
  sdcard/Android/data/...
 */
