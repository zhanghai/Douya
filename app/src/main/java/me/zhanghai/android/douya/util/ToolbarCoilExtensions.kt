/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import coil.Coil
import coil.ImageLoader
import coil.annotation.ExperimentalCoil
import coil.api.load
import coil.request.LoadRequestBuilder
import coil.request.RequestDisposable
import coil.target.PoolableViewTarget
import coil.transition.TransitionTarget
import coil.util.CoilUtils

@OptIn(ExperimentalCoil::class)
fun Toolbar.clear() {
    CoilUtils.clear(this)
}

inline fun Toolbar.loadNavigationIcon(
    uri: String?,
    imageLoader: ImageLoader = Coil.loader(),
    builder: LoadRequestBuilder.() -> Unit = {}
): RequestDisposable {
    return imageLoader.load(context, uri) {
        target(ToolbarNavigationIconTarget(this@loadNavigationIcon))
        builder()
    }
}

@OptIn(ExperimentalCoil::class)
class ToolbarNavigationIconTarget(
    override val view: Toolbar
) : PoolableViewTarget<Toolbar>, TransitionTarget<Toolbar>, DefaultLifecycleObserver {

    private var isStarted = false

    override val drawable: Drawable?
        get() = view.navigationIcon

    override fun onStart(placeholder: Drawable?) {
        setDrawable(placeholder)
    }

    override fun onSuccess(result: Drawable) {
        setDrawable(result)
    }

    override fun onError(error: Drawable?) {
        setDrawable(error)
    }

    override fun onClear() {
        setDrawable(null)
    }

    override fun onStart(owner: LifecycleOwner) {
        isStarted = true
        updateAnimation()
    }

    override fun onStop(owner: LifecycleOwner) {
        isStarted = false
        updateAnimation()
    }

    /** Replace the [Toolbar]'s current navigation icon with [drawable]. */
    private fun setDrawable(drawable: Drawable?) {
        (view.navigationIcon as? Animatable)?.stop()
        view.navigationIcon = drawable
        updateAnimation()
    }

    /** Start/stop the current [Drawable]'s animation based on the current lifecycle state. */
    private fun updateAnimation() {
        val animatable = view.navigationIcon as? Animatable ?: return
        if (isStarted) {
            animatable.start()
        } else {
            animatable.stop()
        }
    }
}
