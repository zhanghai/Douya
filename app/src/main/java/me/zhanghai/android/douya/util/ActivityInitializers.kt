/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.view.View

fun layoutActivityEdgeToEdge(application: Application) {
    application.registerActivityLifecycleCallbacks(object : SimpleActivityLifecycleCallbacks() {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            activity.window.decorView.run {
                layoutInStatusBar = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    layoutInNavigation = true
                }
            }
        }
    })
}

fun ensureActivitySubDecor(application: Application) {
    application.registerActivityLifecycleCallbacks(object : SimpleActivityLifecycleCallbacks() {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            activity.findViewById<View>(android.R.id.content)
        }
    })
}
