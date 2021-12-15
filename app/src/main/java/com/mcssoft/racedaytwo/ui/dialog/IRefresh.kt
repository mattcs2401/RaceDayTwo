package com.mcssoft.racedaytwo.ui.dialog

/**
 * Interface between the MainActivity and the RefreshDialog. Implemented by the MainActivity and
 * passed as a constructor parameter to the RefreshDialog class.
 */
interface IRefresh {
    // Dialog OK button was selected.
    fun refreshOk()

    // Dialog CANCEL button was selected.
    fun refreshCancel() {}     // TBA - basically do nothing.
}