package com.mcssoft.racedaytwo.adapter.meeting

import com.mcssoft.racedaytwo.entity.cache.MeetingCacheEntity

/**
 * Interface between the MeetingsFragment and MeetingAdapter. Implemented by the MeetingsFragment and
 * passed as a constructor parameter to the MeetingAdapter.
 * Note: Basically used to chain IMeetingViewHolder back to the MeetingsFragment.
 */
interface IMeetingAdapter {

    /**
     * Indicate that the recyclerview item was selected.
     * @param meeting: The Meeting.
     */
    fun onSelected(meeting: MeetingCacheEntity)

//    /**
//     * Indicate that the recyclerview item expand was selected.
//     * @param meeting: The Meeting.
//     */
//    fun onExpandSelected(meeting: MeetingCacheEntity)
}