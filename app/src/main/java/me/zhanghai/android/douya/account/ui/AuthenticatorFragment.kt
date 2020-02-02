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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import me.zhanghai.android.douya.account.info.AuthenticatorMode
import me.zhanghai.android.douya.arch.viewModels
import me.zhanghai.android.douya.databinding.AuthenticatorFragmentBinding
import me.zhanghai.android.douya.util.fadeIn
import me.zhanghai.android.douya.util.fadeOut
import me.zhanghai.android.douya.util.startActivitySafe

class AuthenticatorFragment : Fragment() {
    companion object {
        val SIGN_UP_URI = Uri.parse("https://accounts.douban.com/passport/login")
    }

    private val args: AuthenticatorFragmentArgs by navArgs()

    private val viewModel: AuthenticatorViewModel by viewModels {
        { AuthenticatorViewModel(AuthenticatorMode.values()[args.mode], args.username ?: "") }
    }

    private lateinit var binding: AuthenticatorFragmentBinding

    private var resultSent = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AuthenticatorFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        args.response?.onRequestContinued()

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.authenticating.observe(this) { authenticating ->
            viewLifecycleOwner.lifecycleScope.launch {
                if (authenticating) {
                    binding.formLayout.fadeOut()
                    binding.progress.fadeIn()
                } else {
                    binding.progress.fadeOut()
                    binding.formLayout.fadeIn()
                }
            }
        }
        viewModel.signUpEvent.observe(this) {
            startActivitySafe(Intent(Intent.ACTION_VIEW, SIGN_UP_URI))
        }
        viewModel.sendResultAndFinishEvent.observe(this) { result ->
            args.response?.onResult(result.extras)
            resultSent = true
            requireActivity().run {
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
