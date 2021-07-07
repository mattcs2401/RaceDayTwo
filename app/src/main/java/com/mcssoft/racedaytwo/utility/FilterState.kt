package com.mcssoft.racedaytwo.utility

sealed class FilterState<T>(val state: T?, val message: String?) {
    class Initial<T>(initState: T) : FilterState<T>(initState, null)
    class Changed<T>(newState: T) : FilterState<T>(newState, null)
    class Error<T>(message: String) : FilterState<T>(null, message)
}