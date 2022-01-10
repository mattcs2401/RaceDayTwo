package com.mcssoft.racedaytwo.utility

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class ErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val request: Request = chain.request()
        val response = chain.proceed(request)
        when (response.code) {
            400 -> {
                val bp = "bp"
                //Show Bad Request Error Message
            }
            401 -> {
                val bp = "bp"
                //Show UnauthorizedError Message
            }

            403 -> {
                val bp = "bp"
                //Show Forbidden Message
            }

            404 -> {
                val bp = "bp"
                //Show NotFound Message
            }
            else -> {
                val bp = "bp"

            }

            // ... and so on

        }
        return response
    }
}