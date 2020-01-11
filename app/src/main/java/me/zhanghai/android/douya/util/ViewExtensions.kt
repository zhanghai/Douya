/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

import android.view.View

var View.layoutInStatusBar
    get() = systemUiVisibility.hasBits(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    set(value) {
        systemUiVisibility = if (value) {
            systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        } else {
            systemUiVisibility andInv View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }

var View.layoutInNavigation
    get() = systemUiVisibility.hasBits(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
    set(value) {
        systemUiVisibility = if (value) {
            systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        } else {
            systemUiVisibility andInv View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
    }

val View.visible: Boolean
    get() = visibility == View.VISIBLE

fun View.setVisible(visible: Boolean, gone: Boolean = false) {
    visibility = if (visible) View.VISIBLE else if (gone) View.GONE else View.INVISIBLE
}

suspend fun View.fadeIn(force: Boolean = false) {
    if (!visible) {
        alpha = 0f
    }
    setVisible(true)
    animate().run {
        alpha(1f)
        if (!(force || isLaidOut) || (visible && alpha == 1f)) {
            duration = 0
        } else {
            duration = context.shortAnimTime()
            interpolator = context.getInterpolator(android.R.interpolator.fast_out_slow_in)
        }
        start()
        awaitEnd()
    }
}

suspend fun View.fadeOut(force: Boolean = false, gone: Boolean = false) {
    animate().run {
        alpha(0f)
        if (!(force || isLaidOut) || (!visible || alpha == 0f)) {
            duration = 0
        } else {
            duration = context.shortAnimTime()
            interpolator = context.getInterpolator(android.R.interpolator.fast_out_linear_in)
        }
        start()
        awaitEnd()
    }
    setVisible(false, gone)
}
