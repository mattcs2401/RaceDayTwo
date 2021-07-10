package com.mcssoft.racedaytwo.interfaces

/**
 * Interface between the MeetingsFragment and RaceMeetingAdapter. Implemented by the MeetingsFragment and
 * passed as a constructor parameter to the RaceMeetingAdapter.
 * Note: Basically used to chain IViewHolder back to the MeetingsFragment.
 */
interface IAdapter {

    /**
     * Indicate that the "Details" of the expanded recyclerview item was selected.
     */
    fun onDetailsSelected()
}