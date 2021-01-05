package com.mcssoft.racedaytwo.retrofit

import android.content.Context
import com.mcssoft.racedaytwo.R
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Inject


class RetrofitService @Inject constructor (context: Context) {

    private val client = OkHttpClient.Builder().build()
    private val baseUrl = context.getString(R.string.base_url)

    fun <S> createService(serviceClass: Class<S>): S {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .build()
        return retrofit.create(serviceClass)
    }
}