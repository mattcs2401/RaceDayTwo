package com.mcssoft.racedaytwo.entity.events

/**
 * Utility class to model that a certain Runner was selected in the Runner's listing.
 * @param runnerNo: The Runner's number (passed into the object by the RunnerHeaderViewHolder).
 */
data class SelectedRunner(val runnerNo: String) {
    // Updated by the RunnersFragment from (Race) nav arg values.
    var raceId: Long? = null
    // Updated by the RunnerHeaderViewHolder.
    var selected: Boolean = false
}
