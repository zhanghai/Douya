/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

import android.content.Context
import android.view.ViewConfiguration
import androidx.recyclerview.widget.RecyclerView

abstract class OnVerticalScrollListener : RecyclerView.OnScrollListener() {
    final override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (!recyclerView.canScrollVertically(-1)) {
            onScrolledToTop()
        } else if (!recyclerView.canScrollVertically(1)) {
            onScrolledToBottom()
        }
        if (dy < 0) {
            onScrolledUp(dy)
        } else if (dy > 0) {
            onScrolledDown(dy)
        }
        onScrolled(dy)
    }

    open fun onScrolled(dy: Int) {}

    open fun onScrolledUp(dy: Int) {}

    open fun onScrolledDown(dy: Int) {}

    open fun onScrolledToTop() {}

    open fun onScrolledToBottom() {}
}

abstract class OnVerticalScrollWithPagingTouchSlopListener(
    context: Context
) : OnVerticalScrollListener() {
    private val mPagingTouchSlop = ViewConfiguration.get(context).scaledPagingTouchSlop

    // Distance in y since last idle or direction change.
    private var accumulatedDy = 0

    final override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            accumulatedDy = 0
        }
    }

    final override fun onScrolledUp(dy: Int) {
        accumulatedDy = if (accumulatedDy < 0) accumulatedDy + dy else dy
        if (accumulatedDy < -mPagingTouchSlop) {
            onScrolledUp()
        }
    }

    final override fun onScrolledDown(dy: Int) {
        accumulatedDy = if (accumulatedDy > 0) accumulatedDy + dy else dy
        if (accumulatedDy > mPagingTouchSlop) {
            onScrolledDown()
        }
    }

    open fun onScrolledUp() {}

    open fun onScrolledDown() {}
}
