/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

import androidx.recyclerview.widget.RecyclerView

abstract class ListAdapter<T, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
    private val _items = mutableListOf<T>()
    var items: List<T>
        get() = _items
        set(value) = _items.run {
            clear()
            addAll(value)
        }

    override fun getItemCount() = items.size
}
