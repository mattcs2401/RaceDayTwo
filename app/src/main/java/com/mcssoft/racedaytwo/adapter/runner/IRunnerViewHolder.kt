package com.mcssoft.racedaytwo.adapter.runner

import com.mcssoft.racedaytwo.entity.events.SelectedRunner

/**
 * Interface between the RunnerAdapter and the ViewHolders. Implemented by the RunnerAdapter and
 * passed as a constructor parameter to the ViewHolders.
 */
interface IRunnerViewHolder {
    /**
     * The selected Runner from the Runners listing.
     * @param selectedRunner: The Runner.
     */
    fun onRunnerSelected(selectedRunner: SelectedRunner)
}