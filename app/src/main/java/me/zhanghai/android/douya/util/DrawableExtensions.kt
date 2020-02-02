/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.graphics.drawable.PaintDrawable
import android.graphics.drawable.ShapeDrawable.ShaderFactory
import android.graphics.drawable.shapes.RectShape
import android.view.Gravity
import kotlin.math.pow
import kotlin.math.roundToInt

object Drawables

fun Drawables.createScrim(
    gravity: Int,
    color: Int = Color.BLACK,
    stopCount: Int = 2
): Drawable = PaintDrawable().apply {
    shape = RectShape()
    val horizontalGravity = gravity and Gravity.HORIZONTAL_GRAVITY_MASK
    val x0 = if (horizontalGravity == Gravity.LEFT) 1f else 0f
    val x1 = if (horizontalGravity == Gravity.RIGHT) 1f else 0f
    val verticalGravity = gravity and Gravity.VERTICAL_GRAVITY_MASK
    val y0 = if (verticalGravity == Gravity.TOP) 1f else 0f
    val y1 = if (verticalGravity == Gravity.BOTTOM) 1f else 0f
    val alpha = Color.alpha(color)
    val red = Color.red(color)
    val green = Color.green(color)
    val blue = Color.blue(color)
    val stopColors = (0 until stopCount).map {
        val x = it * 1f / (stopCount - 1)
        val opacity = x.pow(3f).coerceIn(0f, 1f)
        Color.argb((opacity * alpha).roundToInt(), red, green, blue)
    }.toIntArray()
    shaderFactory = object : ShaderFactory() {
        override fun resize(width: Int, height: Int) = LinearGradient(
            width * x0, height * y0, width * x1, height * y1, stopColors, null,
            Shader.TileMode.CLAMP
        )
    }
    this.alpha = (0.4f * 255).roundToInt()
}
