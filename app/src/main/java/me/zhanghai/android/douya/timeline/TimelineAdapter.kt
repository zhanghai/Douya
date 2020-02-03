/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.timeline

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import me.zhanghai.android.douya.api.info.TimelineItem
import me.zhanghai.android.douya.arch.MutableLiveData
import me.zhanghai.android.douya.arch.ResumedLifecycleOwner
import me.zhanghai.android.douya.arch.mapDistinct
import me.zhanghai.android.douya.databinding.TimelineItemBinding
import me.zhanghai.android.douya.util.ListAdapter
import me.zhanghai.android.douya.util.layoutInflater

class TimelineAdapter : ListAdapter<TimelineItem, TimelineAdapter.ViewHolder>() {
    init {
        setHasStableIds(true)
    }

    fun createDiffCallback(newItems: List<TimelineItem>): DiffUtil.Callback =
        object : DiffUtil.Callback() {
            override fun getOldListSize() = itemCount

            override fun getNewListSize() = newItems.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                items[oldItemPosition].uid == newItems[newItemPosition].uid

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                items[oldItemPosition] == newItems[newItemPosition]
        }

    override fun getItemId(position: Int): Long = items[position].uid.hashCode().toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(TimelineItemBinding.inflate(parent.context.layoutInflater, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setTimelineItem(items[position])
    }

    class ViewHolder(
        private val binding: TimelineItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val lifecycleOwner = ResumedLifecycleOwner()

        private val viewModel = ViewModel()

        init {
            binding.lifecycleOwner = lifecycleOwner
            binding.viewModel = viewModel
        }

        fun setTimelineItem(timelineItem: TimelineItem?) {
            viewModel.setTimelineItem(timelineItem)
            binding.executePendingBindings()
        }

        class ViewModel {
            data class State(
                val timelineItem: TimelineItem?
            )

            private val state = MutableLiveData(
                State(
                    timelineItem = null
                )
            )

            val timelineItem = state.mapDistinct { it.timelineItem }

            fun setTimelineItem(timelineItem: TimelineItem?) {
                state.value = State(
                    timelineItem = timelineItem
                )
            }
        }
    }
}
