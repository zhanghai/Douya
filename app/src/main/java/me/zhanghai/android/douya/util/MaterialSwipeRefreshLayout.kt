/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.Dimension
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.elevation.ElevationOverlayProvider
import me.zhanghai.android.douya.R

class MaterialSwipeRefreshLayout : SwipeRefreshLayout {
    companion object {
        /**
         * @see androidx.swiperefreshlayout.widget.CircleImageView.SHADOW_ELEVATION
         */
        private const val ELEVATION_DP = 4

        /**
         * @see androidx.swiperefreshlayout.widget.SwipeRefreshLayout.DEFAULT_CIRCLE_TARGET
         */
        private const val PROGRESS_END_DP = 64
    }

    init {
        setColorSchemeColors(context.getColorByAttr(R.attr.colorSecondary))
        setProgressBackgroundColorSchemeColor(
            ElevationOverlayProvider(context).compositeOverlayWithThemeSurfaceColorIfNeeded(
                context.dpToPixel(ELEVATION_DP)
            )
        )
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    fun setProgressOffset(@Dimension offset: Int) {
        setProgressViewOffset(false, offset, offset + context.dpToPixelOffset(PROGRESS_END_DP))
    }
}
