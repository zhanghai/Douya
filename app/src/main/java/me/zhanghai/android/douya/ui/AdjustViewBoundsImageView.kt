/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/**
 * Fixes setAdjustViewBounds() and onMeasure() not respecting minimum sizes when adjusting view
 * bounds.
 */
open class AdjustViewBoundsImageView : AppCompatImageView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    override fun setAdjustViewBounds(adjustViewBounds: Boolean) {
        val scaleType = scaleType
        super.setAdjustViewBounds(adjustViewBounds)
        setScaleType(scaleType)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val measuredWidth = measuredWidth
        val width = measuredWidth.coerceAtLeast(suggestedMinimumWidth)
        val measuredHeight = measuredHeight
        val height = measuredHeight.coerceAtLeast(suggestedMinimumHeight)
        if (measuredWidth != width || measuredHeight != height) {
            setMeasuredDimension(width, height)
        }
    }
}
