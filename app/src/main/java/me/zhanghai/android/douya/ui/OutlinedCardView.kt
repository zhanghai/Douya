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
import me.zhanghai.android.douya.R
import me.zhanghai.android.douya.compat.getColorStateListCompat
import me.zhanghai.android.douya.util.getDimensionPixelSize

class OutlinedCardView : MaterialCardView {

    private val outlineStrokeColor =
        context.getColorStateListCompat(R.color.mtrl_btn_stroke_color_selector)
    private val outlineStrokeWidth =
        context.getDimensionPixelSize(R.dimen.mtrl_btn_stroke_size)
    private val outlineStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    init {
        setCardBackgroundColor(Color.TRANSPARENT)
        cardElevation = 0f
        if (radius != 0f) {
            setStrokeColor(outlineStrokeColor)
            strokeWidth = outlineStrokeWidth
        }
    }

    override fun onDrawForeground(canvas: Canvas) {
        super.onDrawForeground(canvas)

        if (radius == 0f) {
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
