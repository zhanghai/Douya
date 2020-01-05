/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.account.ui

import android.accounts.AccountManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import me.zhanghai.android.douya.R
import me.zhanghai.android.douya.databinding.AuthenticationFragmentBinding

class AuthenticationFragment : Fragment() {

    private val args: AuthenticationFragmentArgs by navArgs()

    private lateinit var binding: AuthenticationFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AuthenticationFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        args.response?.onRequestContinued()

        //binding.viewModel =
    }

    private fun setResult(result: Bundle?) {
        if (result != null) {
            args.response?.onResult(result)
        } else {
            args.response?.onError(AccountManager.ERROR_CODE_CANCELED, "canceled")
        }
    }
}
