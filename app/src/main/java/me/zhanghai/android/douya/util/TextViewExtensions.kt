/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

import android.widget.TextView

fun TextView.setSpanClickable() {
    val wasClickable = isClickable
    val wasLongClickable = isLongClickable
    movementMethod = ClickableMovementMethod
    // Reset for TextView.fixFocusableAndClickableSettings(). We don't want View.onTouchEvent()
    // to consume touch events.
    isClickable = wasClickable
    isLongClickable = wasLongClickable
}
