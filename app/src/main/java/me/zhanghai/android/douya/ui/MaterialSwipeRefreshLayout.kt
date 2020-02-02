/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.Dimension
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.elevation.ElevationOverlayProvider
import me.zhanghai.android.douya.R
import me.zhanghai.android.douya.util.dpToDimension
import me.zhanghai.android.douya.util.dpToDimensionPixelOffset
import me.zhanghai.android.douya.util.getColorByAttr

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

    @Dimension
    var progressOffset = 0
        set(value) {
            if (field == value) {
                return
            }
            field = value
            val start = value - progressCircleDiameter
            setProgressViewOffset(
                false, start, start + context.dpToDimensionPixelOffset(
                PROGRESS_END_DP
            )
            )
        }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    init {
        setColorSchemeColors(context.getColorByAttr(R.attr.colorSecondary))
        setProgressBackgroundColorSchemeColor(
            ElevationOverlayProvider(context).compositeOverlayWithThemeSurfaceColorIfNeeded(
                context.dpToDimension(
                    ELEVATION_DP
                )
            )
        )
    }
}
