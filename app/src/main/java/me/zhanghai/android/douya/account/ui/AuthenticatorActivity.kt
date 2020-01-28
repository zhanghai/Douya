/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.account.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.navigation.fragment.NavHostFragment
import me.zhanghai.android.douya.R

class AuthenticatorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                NavHostFragment.create(R.navigation.authenticator, intent.extras).let {
                    add(android.R.id.content, it)
                    setPrimaryNavigationFragment(it)
                }
            }
        }
    }

    override fun finish() {
        val navHostFragment = supportFragmentManager.primaryNavigationFragment as NavHostFragment
        val fragment = navHostFragment.childFragmentManager.primaryNavigationFragment
            as AuthenticatorFragment
        fragment.onActivityFinish()

        super.finish()
    }
}
