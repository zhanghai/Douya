/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

import androidx.recyclerview.widget.RecyclerView

val RecyclerView.hasFirstItemReachedTop: Boolean
    get() {
        val layoutManager = layoutManager!!
        return layoutManager.findViewByPosition(0)?.let { it.top <= top }
            ?: (layoutManager.childCount > 0)
    }
