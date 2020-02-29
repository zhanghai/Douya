/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import com.google.android.material.card.MaterialCardView

class FullWidthCardView : MaterialCardView {

    private val outlineStrokeColor: ColorStateList?
    private val outlineStrokeWidth: Int
    private val outlineStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    init {
        if (radius == 0f) {
            outlineStrokeColor = strokeColorStateList
            outlineStrokeWidth = strokeWidth
            strokeColor = Color.TRANSPARENT
            strokeWidth = 0
        } else {
            outlineStrokeColor = null
            outlineStrokeWidth = 0
        }
    }

    override fun onDrawForeground(canvas: Canvas) {
        super.onDrawForeground(canvas)

        if (outlineStrokeColor != null && outlineStrokeWidth != 0) {
            outlineStrokePaint.color = outlineStrokeColor.getColorForState(
                drawableState, Color.TRANSPARENT
            )
            canvas.drawRect(
                0f, 0f, width.toFloat(), outlineStrokeWidth.toFloat(), outlineStrokePaint
            )
            canvas.drawRect(
                0f, (height - outlineStrokeWidth).toFloat(), width.toFloat(), height.toFloat(),
                outlineStrokePaint
            )
        }
    }
}
