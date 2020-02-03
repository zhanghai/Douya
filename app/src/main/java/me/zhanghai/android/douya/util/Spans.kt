/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.drawable.Drawable
import android.text.style.ReplacementSpan
import kotlin.math.roundToInt

class DrawableSpan(private val mDrawable: Drawable) : ReplacementSpan() {

    private var topFix = 0
    private var bottomFix = 0

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: FontMetricsInt?
    ): Int {
        if (fm != null) {
            // Important in the case when icon is the first character.
            val oldTop = fm.top
            val oldBottom = fm.bottom
            paint.getFontMetricsInt(fm)
            topFix = fm.top - oldTop
            bottomFix = fm.bottom - oldBottom
            val height = fm.descent - fm.ascent
            val width = if (mDrawable.intrinsicHeight > 0) {
                (height.toFloat() / mDrawable.intrinsicHeight * mDrawable.intrinsicWidth)
                    .roundToInt()
            } else {
                height
            }
            mDrawable.setBounds(0, 0, width, height)
            // fm.ascent and fm.descent can affect top and bottom in draw().
            fm.ascent = fm.top
            fm.descent = fm.bottom
        }
        return mDrawable.bounds.right
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        canvas.save()
        val bounds = mDrawable.bounds
        // Center between top and bottom.
        val dy = (top + topFix + bottom + bottomFix - bounds.top - bounds.bottom).toFloat() / 2
        canvas.translate(x, dy)
        mDrawable.draw(canvas)
        canvas.restore()
    }
}

class SpaceSpan(private val mWidthEm: Float) : ReplacementSpan() {
    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: FontMetricsInt?
    ): Int = (mWidthEm * paint.textSize).roundToInt()

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {}
}
