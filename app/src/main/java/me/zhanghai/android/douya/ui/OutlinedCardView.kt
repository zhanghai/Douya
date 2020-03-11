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
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    init {
        setCardBackgroundColor(Color.TRANSPARENT)
        cardElevation = 0f
        setStrokeColor(context.getColorStateListCompat(R.color.mtrl_btn_stroke_color_selector))
        strokeWidth = context.getDimensionPixelSize(R.dimen.mtrl_btn_stroke_size)
    }
}
