package com.mcssoft.racedaytwo.retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface IFileDownload {

    /**
     * Download the file using the dynamic Url functionality of retrofit (i.e. no end point in the
     * @GET). The ResponseBody holds the payload.
     */
    @GET
    fun downloadFile(@Url fileUrl: String): Call<ResponseBody?>?
}