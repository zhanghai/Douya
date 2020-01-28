/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

import android.content.Context
import android.util.AttributeSet
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import me.zhanghai.android.douya.R

class ThemedSwipeRefreshLayout : SwipeRefreshLayout {
    init {
        setColorSchemeColors(context.getColorByAttr(R.attr.colorSecondary))
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
}
