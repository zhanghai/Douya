/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import me.zhanghai.android.douya.compat.layoutDirectionCompat

class OverlayBackgroundView : View {

    private val overlay: Drawable

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    constructor(context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        setWillNotDraw(true)
        overlay = background
        background = null
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        (parent as View).overlay.add(overlay)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        (parent as View).overlay.remove(overlay)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        overlay.setBounds(left, top, right, bottom)
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)

        if (overlay.isVisible != isVisible) {
            overlay.setVisible(isVisible, false)
        }
    }

    override fun onRtlPropertiesChanged(layoutDirection: Int) {
        super.onRtlPropertiesChanged(layoutDirection)

        overlay.layoutDirectionCompat = layoutDirection
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()

        if (overlay.isStateful) {
            overlay.state = drawableState
        }
    }

    override fun drawableHotspotChanged(x: Float, y: Float) {
        super.drawableHotspotChanged(x, y)

        overlay.setHotspot(x, y)
    }

    override fun jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState()

        overlay.jumpToCurrentState()
    }
}
