package com.mcssoft.racedaytwo.interfaces

/**
 * Interface between the RaceMeetingAdapter and the ViewHolders. Implemented by the
 * RaceMeetingAdapter and passed as a constructor parameter to the ViewHolders.
 */
interface IViewHolder {
    /**
     * Signify that an expand or collapse was done on an adapter item.
     * @param vhType: The view type to revert to.
     * @param position: The adapter position.
     */
    fun onViewHolderSelect(vhType: Int, position: Int)

    /**
     * Indicate that the "Details" of the expanded recyclerview item was selected.
     */
    fun onDetailsSelect()

}