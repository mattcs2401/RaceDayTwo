package com.mcssoft.racedaytwo.utility

/**
 * Class to model the state of the R, T and G toggle buttons of the MainFragment UI.
 * @param rToggle: The R toggle button.
 * @param tToggle: The T toggle button.
 * @param gToggle: The G toggle button.
 * @notes: For all, true == toggled on, else false.
 */
data class ToggledState(
    val rToggle: Boolean,
    val tToggle: Boolean,
    val gToggle: Boolean) {

    // TBA.
    fun getState(): List<Boolean> = arrayListOf(rToggle, tToggle, gToggle)
}
