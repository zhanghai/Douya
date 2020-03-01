/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

inline fun <reified T : View> View.findViewByClass(): T? = findViewByClass(T::class.java)

fun <T : View> View.findViewByClass(clazz: Class<T>): T? {
    if (clazz.isInstance(this)) {
        @Suppress("UNCHECKED_CAST")
        return this as T
    }
    if (this is ViewGroup) {
        children.forEach {
            it.findViewByClass(clazz)?.let { return it }
        }
    }
    return null
}

var View.layoutInStatusBar: Boolean
    get() = systemUiVisibility.hasBits(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    set(value) {
        systemUiVisibility = if (value) {
            systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        } else {
            systemUiVisibility andInv View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }

var View.layoutInNavigation: Boolean
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
        if (!(isLaidOut || force) || (visible && alpha == 1f)) {
            duration = 0
        } else {
            duration = context.shortAnimTime
            interpolator = context.getInterpolator(android.R.interpolator.fast_out_slow_in)
        }
        start()
        awaitEnd()
    }
}

fun View.fadeInUnsafe(force: Boolean = false) {
    GlobalScope.launch(Dispatchers.Main.immediate) {
        fadeIn(force)
    }
}

suspend fun View.fadeOut(force: Boolean = false, gone: Boolean = false) {
    animate().run {
        alpha(0f)
        if (!(isLaidOut || force) || (!visible || alpha == 0f)) {
            duration = 0
        } else {
            duration = context.shortAnimTime
            interpolator = context.getInterpolator(android.R.interpolator.fast_out_linear_in)
        }
        start()
        awaitEnd()
    }
    setVisible(false, gone)
}

fun View.fadeOutUnsafe(force: Boolean = false, gone: Boolean = false) {
    GlobalScope.launch(Dispatchers.Main.immediate) {
        fadeOut(force, gone)
    }
}

suspend fun View.fadeToVisibility(visible: Boolean, force: Boolean = false, gone: Boolean = false) {
    if (visible) {
        fadeIn(force)
    } else {
        fadeOut(force, gone)
    }
}

fun View.fadeToVisibilityUnsafe(visible: Boolean, force: Boolean = false, gone: Boolean = false) {
    GlobalScope.launch(Dispatchers.Main.immediate) {
        fadeToVisibility(visible, force, gone)
    }
}

@SuppressLint("RtlHardcoded")
suspend fun View.slideIn(gravity: Int, force: Boolean = false) {
    setVisible(true)
    animate().run {
        when (Gravity.getAbsoluteGravity(gravity, layoutDirection)) {
            Gravity.LEFT, Gravity.RIGHT -> translationX(0f)
            Gravity.TOP, Gravity.BOTTOM -> translationY(0f)
        }
        if (!(isLaidOut || force)) {
            duration = 0
        } else {
            duration = context.shortAnimTime
            interpolator = context.getInterpolator(android.R.interpolator.fast_out_slow_in)
        }
        start()
        awaitEnd()
    }
}

suspend fun View.slideInUnsafe(gravity: Int, force: Boolean = false) {
    GlobalScope.launch(Dispatchers.Main.immediate) {
        slideIn(gravity, force)
    }
}

@SuppressLint("RtlHardcoded")
suspend fun View.slideOut(gravity: Int, force: Boolean = false, gone: Boolean = false) {
    animate().run {
        when (Gravity.getAbsoluteGravity(gravity, layoutDirection)) {
            Gravity.LEFT -> translationX((-right).toFloat())
            Gravity.RIGHT -> translationX(((parent as View).width - left).toFloat())
            Gravity.TOP -> translationY((-bottom).toFloat())
            Gravity.BOTTOM -> translationY(((parent as View).height - top).toFloat())
        }
        if (!(isLaidOut || force)) {
            duration = 0
        } else {
            duration = context.shortAnimTime
            interpolator = context.getInterpolator(android.R.interpolator.fast_out_linear_in)
        }
        start()
        awaitEnd()
    }
    setVisible(false, gone)
}

fun View.slideOutUnsafe(gravity: Int, force: Boolean = false, gone: Boolean = false) {
    GlobalScope.launch(Dispatchers.Main.immediate) {
        slideOut(gravity, force, gone)
    }
}

suspend fun View.slideToVisibility(
    gravity: Int,
    visible: Boolean,
    force: Boolean = false,
    gone: Boolean = false
) {
    if (visible) {
        slideIn(gravity, force)
    } else {
        slideOut(gravity, force, gone)
    }
}

fun View.slideToVisibilityUnsafe(
    gravity: Int,
    visible: Boolean,
    force: Boolean = false,
    gone: Boolean = false
) {
    GlobalScope.launch(Dispatchers.Main.immediate) {
        slideToVisibility(gravity, visible, force, gone)
    }
}
