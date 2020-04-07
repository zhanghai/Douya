/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

import android.annotation.TargetApi
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.BlendMode
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Outline
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import org.xmlpull.v1.XmlPullParser

@Suppress("DEPRECATION", "UsePropertyAccessSyntax")
open class DrawableWrapper : Drawable, Drawable.Callback {
    protected open val creator
        get() = CREATOR

    private var state: ConstantState

    private val drawable: Drawable

    private var mutated = false

    constructor(drawable: Drawable) : super() {
        state = ConstantState(creator, drawable.constantState ?: DummyDrawableState(drawable))
        this.drawable = drawable
        drawable.callback = this
    }

    protected constructor(
        state: ConstantState,
        resources: Resources?,
        theme: Resources.Theme?
    ) : super() {
        this.state = state
        drawable = state.drawableState.newDrawable(resources, theme)
        drawable.callback = this
    }

    override fun unscheduleSelf(what: Runnable) = drawable.unscheduleSelf(what)

    override fun canApplyTheme() = drawable.canApplyTheme()

    override fun draw(canvas: Canvas) = drawable.draw(canvas)

    override fun setChangingConfigurations(configs: Int) =
        drawable.setChangingConfigurations(configs)

    override fun setState(stateSet: IntArray) = drawable.setState(stateSet)

    override fun scheduleSelf(what: Runnable, `when`: Long) = drawable.scheduleSelf(what, `when`)

    @RequiresApi(Build.VERSION_CODES.M)
    override fun getLayoutDirection() = drawable.getLayoutDirection()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onLayoutDirectionChanged(layoutDirection: Int) =
        drawable.onLayoutDirectionChanged(layoutDirection)

    override fun getMinimumWidth() = drawable.getMinimumWidth()

    override fun getAlpha() = drawable.getAlpha()

    override fun setAlpha(alpha: Int) = drawable.setAlpha(alpha)

    override fun setTint(tintColor: Int) = drawable.setTint(tintColor)

    override fun getDirtyBounds() = drawable.getDirtyBounds()

    override fun getColorFilter() = drawable.getColorFilter()

    override fun clearColorFilter() = drawable.clearColorFilter()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun setTintBlendMode(blendMode: BlendMode?) = drawable.setTintBlendMode(blendMode)

    override fun onLevelChange(level: Int): Boolean = drawable.setLevel(level)

    override fun getMinimumHeight() = drawable.getMinimumHeight()

    override fun setAutoMirrored(mirrored: Boolean) = drawable.setAutoMirrored(mirrored)

    override fun getOpacity() = drawable.getOpacity()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun isProjected() = drawable.isProjected()

    override fun setVisible(visible: Boolean, restart: Boolean) =
        drawable.setVisible(visible, restart)

    override fun setTintMode(tintMode: PorterDuff.Mode?) = drawable.setTintMode(tintMode)

    override fun applyTheme(t: Resources.Theme) = drawable.applyTheme(t)

    override fun inflate(r: Resources, parser: XmlPullParser, attrs: AttributeSet) =
        drawable.inflate(r, parser, attrs)

    override fun inflate(
        r: Resources,
        parser: XmlPullParser,
        attrs: AttributeSet,
        theme: Resources.Theme?
    ) = drawable.inflate(r, parser, attrs, theme)

    override fun setDither(dither: Boolean) = drawable.setDither(dither)

    override fun setFilterBitmap(filter: Boolean) = drawable.setFilterBitmap(filter)

    override fun getCurrent() = drawable.getCurrent()

    override fun getIntrinsicWidth() = drawable.getIntrinsicWidth()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun getOpticalInsets() = drawable.getOpticalInsets()

    override fun getOutline(outline: Outline) = drawable.getOutline(outline)

    override fun mutate(): Drawable {
        if (!mutated) {
            state = ConstantState(state)
            drawable.mutate()
            mutated = true
        }
        return this
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) =
        drawable.setBounds(left, top, right, bottom)

    override fun setBounds(bounds: Rect) = drawable.setBounds(bounds)

    override fun setHotspot(x: Float, y: Float) = drawable.setHotspot(x, y)

    override fun getIntrinsicHeight() = drawable.getIntrinsicHeight()

    override fun jumpToCurrentState() = drawable.jumpToCurrentState()

    override fun getChangingConfigurations() = drawable.getChangingConfigurations()

    override fun isStateful() = drawable.isStateful()

    override fun onStateChange(state: IntArray): Boolean {
        return drawable.setState(state)
    }

    override fun getState() = drawable.getState()

    override fun setColorFilter(colorFilter: ColorFilter?) = drawable.setColorFilter(colorFilter)

    override fun setColorFilter(color: Int, mode: PorterDuff.Mode) =
        drawable.setColorFilter(color, mode)

    override fun setHotspotBounds(left: Int, top: Int, right: Int, bottom: Int) =
        drawable.setHotspotBounds(left, top, right, bottom)

    @RequiresApi(Build.VERSION_CODES.M)
    override fun getHotspotBounds(outRect: Rect) = drawable.getHotspotBounds(outRect)

    override fun getTransparentRegion() = drawable.getTransparentRegion()

    override fun getConstantState(): Drawable.ConstantState? = state

    override fun getPadding(padding: Rect) = drawable.getPadding(padding)

    override fun isAutoMirrored() = drawable.isAutoMirrored()

    @TargetApi(Build.VERSION_CODES.M)
    override fun isFilterBitmap() = drawable.isFilterBitmap()

    override fun setTintList(tint: ColorStateList?) = drawable.setTintList(tint)

    override fun unscheduleDrawable(who: Drawable, what: Runnable) {
        callback?.unscheduleDrawable(this, what)
    }

    override fun invalidateDrawable(who: Drawable) {
        callback?.invalidateDrawable(this)
    }

    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
        callback?.scheduleDrawable(this, what, `when`)
    }

    protected class ConstantState(
        private val creator: (ConstantState, Resources?, Resources.Theme?) -> DrawableWrapper,
        val drawableState: Drawable.ConstantState
    ) : Drawable.ConstantState() {
        constructor(state: ConstantState) : this(state.creator, state.drawableState)

        override fun newDrawable() = newDrawable(null)

        override fun newDrawable(resources: Resources?) = newDrawable(resources, null)

        override fun newDrawable(resources: Resources?, theme: Resources.Theme?): Drawable =
            creator(this, resources, theme)

        override fun getChangingConfigurations(): Int = drawableState.changingConfigurations

        override fun canApplyTheme(): Boolean = drawableState.canApplyTheme()
    }

    private class DummyDrawableState(private val drawable: Drawable) : Drawable.ConstantState() {
        override fun newDrawable(): Drawable = drawable

        override fun getChangingConfigurations(): Int = 0
    }

    companion object {
        private val CREATOR: (ConstantState, Resources?, Resources.Theme?) -> DrawableWrapper =
            ::DrawableWrapper
    }
}
