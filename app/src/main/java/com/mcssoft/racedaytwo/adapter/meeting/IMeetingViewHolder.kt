package com.mcssoft.racedaytwo.adapter.meeting

import com.mcssoft.racedaytwo.entity.cache.MeetingCacheEntity

/**
 * Interface between the MeetingAdapter and the ViewHolders. Implemented by the MeetingAdapter and
 * passed as a constructor parameter to the ViewHolders.
 */
interface IMeetingViewHolder {
    /**
     * Signify that an expand or collapse was done on an adapter item.
     * @param vhType: The view type to revert to.
     * @param position: The adapter position.
     */
    fun onExpandCollapseSelect(vhType: Int, position: Int)

    /**
     * Indicate that the expanded Meeting recyclerview item was selected. The Meeting object is
     * passed as a nav args to the RacesFragment and used to display header information for the
     * list of Races.
     * @param meeting: The Meeting.
     */
    fun onExpandedSelect(meeting: MeetingCacheEntity)

}