package com.mcssoft.racedaytwo.adapter.race

import com.mcssoft.racedaytwo.entity.cache.RaceCacheEntity

/**
 * Interface between the RacesFragment and RaceAdapter. Implemented by the RacesFragment and
 * passed as a constructor parameter to the RaceAdapter.
 * Note: Basically used to chain IRaceViewHolder back to the RacesFragment.
 */
interface IRaceAdapter {

    /**
     * Indicates that an item was selected in the list of Races.
     * @param race: The Race.
     */
    fun onRaceSelected(race: RaceCacheEntity)
}