/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

open class MergeAdapter(
    vararg adapters: RecyclerView.Adapter<*>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    @Suppress("UNCHECKED_CAST")
    private val adapters = adapters.asList() as List<RecyclerView.Adapter<RecyclerView.ViewHolder>>

    private val viewTypeToAdapterIndex = mutableMapOf<Int, Int>()

    init {
        super.setHasStableIds(adapters.all { it.hasStableIds() })
        adapters.forEach { it.registerAdapterDataObserver(AdapterDataObserver(it)) }
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        throw UnsupportedOperationException("setHasStableIds() is not supported on MergeAdapter")
    }

    override fun getItemCount(): Int {
        return adapters.sumBy { it.itemCount }
    }

    override fun getItemId(position: Int): Long =
        findAdapter(position) { adapterIndex, adapter, adapterPosition ->
            adapter.getItemId(adapterPosition).let {
                if (it == RecyclerView.NO_ID) it else 31 * adapterIndex + it
            }
        }

    override fun getItemViewType(position: Int): Int =
        findAdapter(position) { adapterIndex, adapter, adapterPosition ->
            ((adapterIndex shl Int.SIZE_BITS / 2) + adapter.getItemViewType(adapterPosition)).also {
                viewTypeToAdapterIndex[it] = adapterIndex
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val adapterIndex = getAdapterIndexForViewType(viewType)
        val adapterViewType = viewType - (adapterIndex shl Int.SIZE_BITS / 2)
        return adapters[adapterIndex].onCreateViewHolder(parent, adapterViewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        findAdapter(position) { _, adapter, adapterPosition ->
            adapter.onBindViewHolder(holder, adapterPosition)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: List<Any>
    ) {
        findAdapter(position) { _, adapter, adapterPosition ->
            adapter.onBindViewHolder(holder, adapterPosition, payloads)
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        val adapterIndex = getAdapterIndexForViewType(holder.itemViewType)
        adapters[adapterIndex].onViewRecycled(holder)
    }

    override fun onFailedToRecycleView(holder: RecyclerView.ViewHolder): Boolean =
        findAdapter(holder.adapterPosition) { _, adapter, _ ->
            adapter.onFailedToRecycleView(holder)
        }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        findAdapter(holder.adapterPosition) { _, adapter, _ ->
            adapter.onViewAttachedToWindow(holder)
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        findAdapter(holder.adapterPosition) { _, adapter, _ ->
            adapter.onViewDetachedFromWindow(holder)
        }
    }

    private inline fun <T> findAdapter(
        position: Int,
        block: (Int, RecyclerView.Adapter<RecyclerView.ViewHolder>, Int) -> T
    ): T {
        var adapterPosition = position
        for ((adapterIndex, adapter) in adapters.withIndex()) {
            val count = adapter.itemCount
            if (adapterPosition < count) {
                return block(adapterIndex, adapter, adapterPosition)
            }
            adapterPosition -= count
        }
        error("Unknown position: $position")
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        adapters.forEach { it.onAttachedToRecyclerView(recyclerView) }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        adapters.forEach { it.onDetachedFromRecyclerView(recyclerView) }
    }

    private fun getAdapterIndexForViewType(viewType: Int): Int =
        viewTypeToAdapterIndex[viewType] ?: error("Unknown viewType: $viewType")

    private inner class AdapterDataObserver(
        private val adapter: RecyclerView.Adapter<*>
    ) : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            notifyDataSetChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            notifyItemRangeChanged(getItemPosition(positionStart), itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            notifyItemRangeChanged(getItemPosition(positionStart), itemCount, payload)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            notifyItemRangeInserted(getItemPosition(positionStart), itemCount)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            notifyItemRangeRemoved(getItemPosition(positionStart), itemCount)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            require(itemCount == 1) { "Moving more than 1 item is unsupported" }
            notifyItemMoved(getItemPosition(fromPosition), getItemPosition(toPosition))
        }

        private fun getItemPosition(position: Int): Int =
            adapters.takeWhile { it !== adapter }.sumBy { it.itemCount } + position
    }
}
