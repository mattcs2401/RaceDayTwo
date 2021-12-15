package com.mcssoft.racedaytwo.adapter.race

import com.mcssoft.racedaytwo.entity.cache.RaceCacheEntity

/**
 * Interface between the RaceAdapter and the ViewHolders. Implemented by the RaceAdapter and passed
 * as a constructor parameter to the ViewHolders.
 */
interface IRaceViewHolder {

    /**
     * Indicate that an item in the Races listing was selected. The Race object is passed as a nav
     * args to the RunnersFragment and used to display header information for the list of Runners.
     * @param race: The Race.
     */
    fun onRaceSelect(race: RaceCacheEntity)
}