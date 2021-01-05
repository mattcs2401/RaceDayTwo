package com.mcssoft.racedaytwo.utiliy

import androidx.activity.OnBackPressedCallback

/**
 * Call back to handle back press by the user.
 */
class RaceDayBackPressCB(enabled: Boolean) : OnBackPressedCallback(enabled) {

    override fun handleOnBackPressed() {
        // Do nothing, basically means back press is ignored.
    }

    fun removeCallback() {
        super.remove()
    }
}