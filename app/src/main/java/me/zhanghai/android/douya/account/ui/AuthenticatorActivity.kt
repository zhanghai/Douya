/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.account.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit

class AuthenticatorActivity : AppCompatActivity() {
    private lateinit var authenticatorFragment: AuthenticatorFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            authenticatorFragment = AuthenticatorFragment().apply { arguments = intent.extras }
            supportFragmentManager.commit { add(android.R.id.content, authenticatorFragment) }
        } else {
            authenticatorFragment = supportFragmentManager.findFragmentById(
                android.R.id.content
            ) as AuthenticatorFragment
        }
    }

    override fun finish() {
        authenticatorFragment.onActivityFinish()

        super.finish()
    }
}
