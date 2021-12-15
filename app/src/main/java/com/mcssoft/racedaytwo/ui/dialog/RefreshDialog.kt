package com.mcssoft.racedaytwo.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.mcssoft.racedaytwo.R

/**
 * Class to display a "warning" message about the Refresh menu option on the bottom nav bar menu.
 * @param iRefresh: Interface back to MainActivity.
 */
class RefreshDialog(private val iRefresh: IRefresh): DialogFragment() {
/* https://developer.android.com/guide/topics/ui/dialogs */

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setTitle(resources.getString(R.string.lbl_delete_all))
                .setMessage(setMessage(requireActivity()))
                .setPositiveButton(R.string.lbl_ok) { _, _ -> iRefresh.refreshOk() }
                .setNegativeButton(R.string.lbl_cancel) { _, _ -> iRefresh.refreshCancel() }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun setMessage(context: Context): String {
        return StringBuilder()
            .append(context.resources.getString(R.string.refresh_msg_l1))
            .appendLine()
            .append(context.resources.getString(R.string.refresh_msg_l2))
            .appendLine()
            .toString()
    }
}