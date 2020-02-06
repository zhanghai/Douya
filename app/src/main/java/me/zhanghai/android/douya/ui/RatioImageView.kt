/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui

import android.content.Context
import android.util.AttributeSet
import kotlin.math.roundToInt

/**
 * An [android.widget.ImageView] that measures with a ratio. Also sets scaleType to centerCrop.
 */
class RatioImageView : AdjustViewBoundsImageView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    init {
        scaleType = ScaleType.CENTER_CROP
    }

    var ratio = 1f
        set(ratio) {
            if (field == ratio) {
                return
            }
            field = ratio
            requestLayout()
            invalidate()
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var coercedWidthMeasureSpec = widthMeasureSpec
        var coercedHeightMeasureSpec = heightMeasureSpec
        if (ratio > 0) {
            if (MeasureSpec.getMode(coercedHeightMeasureSpec) == MeasureSpec.EXACTLY) {
                val height = MeasureSpec.getSize(coercedHeightMeasureSpec)
                val width = (ratio * height).roundToInt().coerceIn(suggestedMinimumWidth, maxWidth)
                coercedWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
            } else {
                val width = MeasureSpec.getSize(coercedWidthMeasureSpec)
                val height = (width / ratio).roundToInt().coerceIn(
                    suggestedMinimumHeight, maxHeight
                )
                coercedHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            }
        }
        super.onMeasure(coercedWidthMeasureSpec, coercedHeightMeasureSpec)
    }
}
