/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import me.zhanghai.android.douya.account.app.ensureActiveAccount
import me.zhanghai.android.douya.app.accountManager
import me.zhanghai.android.douya.timeline.TimelineFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!accountManager.ensureActiveAccount(this)) {
            return
        }

        // TODO
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(android.R.id.content, TimelineFragment())
            }
        }
    }
}
