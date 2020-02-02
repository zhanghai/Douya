/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */
package me.zhanghai.android.douya.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class GutterItemDecoration(val gutterSize: Int) : ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.set(0, 0, 0, 0)
            return
        }
        val orientation = (parent.layoutManager as LinearLayoutManager).orientation
        if (orientation == RecyclerView.VERTICAL) {
            outRect.set(0, gutterSize, 0, 0)
        } else {
            outRect.set(gutterSize, 0, 0, 0)
        }
    }
}
