package com.mcssoft.racedaytwo.adapter.runner

import com.mcssoft.racedaytwo.entity.events.SelectedRunner

/**
 * Interface between the RunnersFragment and RunnersAdapter. Implemented by the RunnersFragment and
 * passed as a constructor parameter to the RunnerAdapter.
 * Note: Basically used to chain IRunnerViewHolder back to the RunnersFragment.
 */
interface IRunnerAdapter {
    /**
     * The selected Runner from the Runners listing.
     * @param selectedRunner: The Runner.
     */
    fun onRunnerSelected(selectedRunner: SelectedRunner)
}