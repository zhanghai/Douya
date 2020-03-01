/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import me.zhanghai.android.douya.util.slideToVisibilityUnsafe

class QuickReturnAppBarLayout : OutlinedAppBarLayout {

    var showing = true
        set(value) {
            if (field == value) {
                return
            }
            field = value
            slideToVisibilityUnsafe(Gravity.TOP, value)
        }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )
}
