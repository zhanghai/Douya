/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.link

import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import me.zhanghai.android.customtabshelper.CustomTabsHelperFragment
import me.zhanghai.android.douya.R
import me.zhanghai.android.douya.util.activity
import me.zhanghai.android.douya.util.createViewIntent
import me.zhanghai.android.douya.util.getColorByAttr
import me.zhanghai.android.douya.util.startActivitySafe
import org.chromium.customtabsclient.CustomTabsActivityHelper

object UriHandler {

    private val customTabsFallback = CustomTabsActivityHelper.CustomTabsFallback { activity, uri ->
        openWithIntent(uri, activity)
    }

    fun open(uri: Uri, context: Context) {
        if (DouyaUriHandler.open(uri, context) || FrodoUriHandler.open(uri, context)) {
            return
        }

        when (uri.scheme) {
            "http", "https", "ftp" -> {
                context.activity?.let {
                    openWithCustomTabs(uri, it)
                    return
                }
            }
        }
        openWithIntent(uri, context)
    }

    fun open(uri: String, context: Context) = open(Uri.parse(uri), context)

    private fun openWithCustomTabs(uri: Uri, activity: Activity) {
        val intent = CustomTabsIntent.Builder()
            .addDefaultShareMenuItem()
            .enableUrlBarHiding()
            .setToolbarColor(activity.getColorByAttr(R.attr.colorSurface))
            .setShowTitle(true)
            .build()
        CustomTabsHelperFragment.open(activity, intent, uri, customTabsFallback)
    }

    private fun openWithIntent(uri: Uri, context: Context) {
        context.startActivitySafe(uri.createViewIntent())
    }
}
