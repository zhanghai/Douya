/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class MoreItemAdapter(
    adapter: RecyclerView.Adapter<*>,
    @LayoutRes private val layoutRes: Int
) : RecyclerView.Adapter<MoreItemAdapter.ViewHolder>() {
    var loading = false
        set(value) {
            if (field == value) {
                return
            }
            field = value
            if (hasItem) {
                val holder = holder
                if (holder != null) {
                    onBindViewHolder(holder, 0)
                } else {
                    notifyItemChanged(0)
                }
            }
        }

    private var hasItem = false
        set(value) {
            if (field == value) {
                return
            }
            field = value
            if (value) {
                notifyItemInserted(0)
            } else {
                notifyItemRemoved(0)
            }
        }

    private var holder: ViewHolder? = null

    init {
        setHasStableIds(true)

        adapter.registerAdapterDataObserver(object : AdapterDataChangeObserver() {
            override fun onChanged() {
                hasItem = adapter.itemCount > 0
            }
        })
    }

    override fun getItemCount(): Int = if (hasItem) 1 else 0

    override fun getItemId(position: Int): Long = 0L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent.context.layoutInflater.inflate(layoutRes, parent, false)).apply {
            (itemView.layoutParams as? StaggeredGridLayoutManager.LayoutParams)?.let {
                it.isFullSpan = true
            }
        }.also {
            holder = it
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.isInvisible = !loading
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)

        this.holder = null
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
