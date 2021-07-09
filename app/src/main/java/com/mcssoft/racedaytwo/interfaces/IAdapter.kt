package com.mcssoft.racedaytwo.interfaces

/**
 * Interface between the MainFragment and RaceMeetingAdapter. Implemented by the MainFragment and
 * passed as a constructor parameter to the RaceMeetingAdapter.
 * Note: Basically used to chain IViewHolder back to the MainFragment.
 */
interface IAdapter {

    /**
     * Indicate that the "Details" of the expanded recyclerview item was selected.
     */
    fun onDetailsSelected()
}