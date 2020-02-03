/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.compat

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.text.style.ImageSpan
import java.lang.ref.WeakReference

class ImageSpanCompat : ImageSpan {
    companion object {
        const val ALIGN_CENTER = 2
    }

    private var mDrawableRef: WeakReference<Drawable>? = null

    @Deprecated("Use ImageSpan(Context, Bitmap) instead")
    @Suppress("DEPRECATION")
    constructor(b: Bitmap) : super(b)

    @Deprecated("Use ImageSpan(Context, Bitmap, Int) instead")
    @Suppress("DEPRECATION")
    constructor(b: Bitmap, verticalAlignment: Int) : super(b, verticalAlignment)

    constructor(context: Context, bitmap: Bitmap) : super(context, bitmap)

    constructor(context: Context, bitmap: Bitmap, verticalAlignment: Int) : super(
        context, bitmap, verticalAlignment
    )

    constructor(drawable: Drawable) : super(drawable)

    constructor(drawable: Drawable, verticalAlignment: Int) : super(drawable, verticalAlignment)

    constructor(drawable: Drawable, source: String) : super(drawable, source)

    constructor(drawable: Drawable, source: String, verticalAlignment: Int) : super(
        drawable, source, verticalAlignment
    )

    constructor(context: Context, uri: Uri) : super(context, uri)

    constructor(context: Context, uri: Uri, verticalAlignment: Int) : super(
        context, uri, verticalAlignment
    )

    constructor(context: Context, resourceId: Int) : super(context, resourceId)

    constructor(context: Context, resourceId: Int, verticalAlignment: Int) : super(
        context, resourceId, verticalAlignment
    )

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q || mVerticalAlignment != ALIGN_CENTER) {
            super.draw(canvas, text, start, end, x, top, y, bottom, paint)
        } else {
            val b: Drawable = getCachedDrawable()
            canvas.save()
            val transY = (bottom - top) / 2 - b.bounds.height() / 2
            canvas.translate(x, transY.toFloat())
            b.draw(canvas)
            canvas.restore()
        }
    }

    private fun getCachedDrawable(): Drawable {
        val wr: WeakReference<Drawable>? = mDrawableRef
        var d = wr?.get()
        if (d == null) {
            d = drawable!!
            mDrawableRef = WeakReference(d)
        }
        return d
    }
}
