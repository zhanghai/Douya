/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.account.ui

import android.accounts.AccountManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs

class AuthenticationFragment : Fragment() {

    private val args: AuthenticationFragmentArgs by navArgs()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        args.response?.onRequestContinued()
    }

    private fun setResult(result: Bundle?) {
        if (result != null) {
            args.response?.onResult(result)
        } else {
            args.response?.onError(AccountManager.ERROR_CODE_CANCELED, "canceled")
        }
    }
}
