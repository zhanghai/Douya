/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.account.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.navigation.fragment.NavHostFragment
import me.zhanghai.android.douya.R

class AuthenticationActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Calls ensureSubDecor().
        findViewById<View>(android.R.id.content)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(android.R.id.content, NavHostFragment.create(R.navigation.authenticaton,
                        intent.extras))
            }
        }
    }
}
