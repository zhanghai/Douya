/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.account.ui

import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import me.zhanghai.android.douya.R
import me.zhanghai.android.douya.account.app.activeAccount
import me.zhanghai.android.douya.account.app.ownAccounts
import me.zhanghai.android.douya.app.accountManager

class SelectAccountDialogFragment : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        val accounts = accountManager.ownAccounts
        return MaterialAlertDialogBuilder(requireContext(), theme)
            .setTitle(R.string.select_account_title)
            .setSingleChoiceItems(accounts.map { it.name }.toTypedArray(), -1) { _, which ->
                accountManager.activeAccount = accounts[which]
                dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        if (accountManager.activeAccount == null) {
            requireActivity().finish()
        }
    }
}
