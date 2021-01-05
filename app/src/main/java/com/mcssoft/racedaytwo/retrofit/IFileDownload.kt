package com.mcssoft.racedaytwo.retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface IFileDownload {

    /**
     * Download the file as a stream (so doesn't keep all in memory) to write to disk. This uses the
     * dynamic Url functionality of retrofit (i.e. no end point in the @GET). The ResponseBody holds
     * the payload.
     */
    @Streaming
    @GET
    fun downloadFile(@Url fileUrl: String): Call<ResponseBody?>?
//    suspend fun downloadFile(@Url fileUrl: String): ResponseBody?  // TBA
}