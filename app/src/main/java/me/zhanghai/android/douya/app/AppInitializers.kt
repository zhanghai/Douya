/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.app

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import com.facebook.stetho.Stetho
import com.jakewharton.threetenabp.AndroidThreeTen
import me.zhanghai.android.customtabshelper.CustomTabsHelperFragment
import me.zhanghai.android.douya.compat.ImageDecoderDecoder
import me.zhanghai.android.douya.util.SimpleActivityLifecycleCallbacks
import me.zhanghai.android.douya.util.layoutInNavigation
import me.zhanghai.android.douya.util.layoutInStatusBar
import timber.log.Timber

val appInitializers = listOf(
    ::initializeThreeTen, ::initializeTimber, ::initializeStetho, ::initializeCoil,
    ::layoutActivityEdgeToEdge, ::ensureActivitySubDecor, ::bindCustomTabsService
)

private fun initializeThreeTen() {
    AndroidThreeTen.init(application)
}

private fun initializeTimber() {
    Timber.plant(Timber.DebugTree())
}

private fun initializeStetho() {
    Stetho.initializeWithDefaults(application)
}

private fun initializeCoil() {
    Coil.setDefaultImageLoader {
        ImageLoader(application) {
            componentRegistry {
                add(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ImageDecoderDecoder()
                    } else {
                        GifDecoder()
                    }
                )
            }
            crossfade(true)
        }
    }
}

private fun layoutActivityEdgeToEdge() {
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

private fun ensureActivitySubDecor() {
    application.registerActivityLifecycleCallbacks(object : SimpleActivityLifecycleCallbacks() {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            activity.findViewById<View>(android.R.id.content)
        }
    })
}

private fun bindCustomTabsService() {
    application.registerActivityLifecycleCallbacks(object : SimpleActivityLifecycleCallbacks() {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            CustomTabsHelperFragment.attachTo(activity as FragmentActivity)
        }
    })
}
