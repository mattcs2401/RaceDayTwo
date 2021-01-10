package com.mcssoft.racedaytwo.retrofit

import android.content.Context
import android.util.Log
import com.mcssoft.racedaytwo.R
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.io.File
import javax.inject.Inject


class RetrofitService @Inject constructor(context: Context) {

    // TBA as to actually required.
    private val cache = Cache(File(context.cacheDir, "name"), 1024 * 1024)

    // TBA (more for testing ATT).
    private fun httpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("TAG", "http logger: $message")
        }
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }

    private val client = OkHttpClient.Builder()
//        .addInterceptor(httpLoggingInterceptor())
        .cache(cache)
        .build()

    private val baseUrl = context.getString(R.string.base_url)

    fun <S> createService(serviceClass: Class<S>): S {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .build()
        return retrofit.create(serviceClass)
    }

}