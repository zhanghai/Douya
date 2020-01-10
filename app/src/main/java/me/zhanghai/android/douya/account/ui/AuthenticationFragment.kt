/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.account.ui

import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import me.zhanghai.android.douya.account.info.AuthenticationMode
import me.zhanghai.android.douya.arch.observe
import me.zhanghai.android.douya.arch.viewModels
import me.zhanghai.android.douya.databinding.AuthenticationFragmentBinding
import me.zhanghai.android.douya.util.startActivitySafely

private val SIGN_UP_URI = Uri.parse("https://accounts.douban.com/passport/login")

class AuthenticationFragment : Fragment() {

    private val args: AuthenticationFragmentArgs by navArgs()

    private val viewModel: AuthenticationViewModel by viewModels {
        { AuthenticationViewModel(AuthenticationMode.values()[args.mode], args.username ?: "") }
    }

    private lateinit var binding: AuthenticationFragmentBinding

    private var resultSent = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AuthenticationFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        args.response?.onRequestContinued()

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.signUpEvent.observe(this) {
            startActivitySafely(Intent(Intent.ACTION_VIEW, SIGN_UP_URI))
        }
        viewModel.sendResultAndFinishEvent.observe(this) { result ->
            args.response?.onResult(result.extras)
            resultSent = true
            with (requireActivity()) {
                setResult(Activity.RESULT_OK, result)
                finish()
            }
        }
    }

    fun onActivityFinish() {
        if (!resultSent) {
            args.response?.onError(AccountManager.ERROR_CODE_CANCELED, "canceled")
        }
    }
}
