/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.app

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.view.View
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.facebook.stetho.Stetho
import me.zhanghai.android.douya.util.SimpleActivityLifecycleCallbacks
import me.zhanghai.android.douya.util.layoutInNavigation
import me.zhanghai.android.douya.util.layoutInStatusBar
import timber.log.Timber

interface AppInitializer {
    fun init(application: Application)
}

val appInitializers = listOf(
    TimberInitializer, StethoInitializer, CoilInitializer, LayoutActivityEdgeToEdgeInitializer,
    EnsureActivitySubDecorInitializer
)

object TimberInitializer : AppInitializer {
    override fun init(application: Application) {
        Timber.plant(Timber.DebugTree())
    }
}

object StethoInitializer : AppInitializer {
    override fun init(application: Application) {
        Stetho.initializeWithDefaults(appContext)
    }
}

object CoilInitializer : AppInitializer {
    override fun init(application: Application) {
        Coil.setDefaultImageLoader {
            ImageLoader(appContext) {
                componentRegistry {
                    add(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ImageDecoderDecoder()
                    } else {
                        GifDecoder()
                    })
                }
                crossfade(true)
            }
        }
    }
}

object LayoutActivityEdgeToEdgeInitializer : AppInitializer {
    override fun init(application: Application) {
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
}

object EnsureActivitySubDecorInitializer : AppInitializer {
    override fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(object : SimpleActivityLifecycleCallbacks() {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activity.findViewById<View>(android.R.id.content)
            }
        })
    }
}

