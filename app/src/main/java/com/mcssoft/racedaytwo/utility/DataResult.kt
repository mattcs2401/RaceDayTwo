package com.mcssoft.racedaytwo.utility

sealed class DataResult<out T> {

    data class Success<out T>(val data: T): DataResult<T>()
//    data class Error(val msg: String, val cause: Exception? = null): DataResult<Nothing>()
    data class Error<out T>(val data: T): DataResult<T>()

}