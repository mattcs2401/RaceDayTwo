package com.mcssoft.racedaytwo.ui.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.mcssoft.racedaytwo.R
import com.mcssoft.racedaytwo.interfaces.IDeleteAll

/**
 * A simple Yes/No type dialog as to whether to bulk delete all race meetings details.
 * An OK button click is returned through the parameter interface.
 */
class DeleteAllDialog(private var iDeleteAll: IDeleteAll) : DialogFragment(), DialogInterface.OnClickListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle(getString(R.string.title_delete_all))
        dialog.setMessage(getString(R.string.message_delete_all))
        dialog.setPositiveButton(getString(R.string.lbl_ok), this)
        dialog.setNegativeButton(getString(R.string.lbl_cancel), this)
        return dialog.create()
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        when(which) {
            DialogInterface.BUTTON_POSITIVE -> {
                iDeleteAll.deleteAll(true)
                this.dialog!!.cancel()
            }
        }
    }
}