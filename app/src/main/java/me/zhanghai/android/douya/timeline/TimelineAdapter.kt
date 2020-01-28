/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.timeline

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.zhanghai.android.douya.api.info.TimelineItem
import me.zhanghai.android.douya.databinding.TimelineItemBinding
import me.zhanghai.android.douya.util.layoutInflater

class TimelineAdapter : ListAdapter<TimelineItem, TimelineAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TimelineItem>() {

            override fun areItemsTheSame(oldItem: TimelineItem, newItem: TimelineItem) =
                oldItem.uid == newItem.uid

            override fun areContentsTheSame(oldItem: TimelineItem, newItem: TimelineItem) =
                oldItem == newItem
        }
    }

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = currentList[position].uid.hashCode().toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TimelineItemBinding.inflate(parent.context.layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class ViewHolder(
        private val binding: TimelineItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(timelineItem: TimelineItem) {
            binding.run {
                this.timelineItem = timelineItem
                executePendingBindings()
            }
        }
    }
}
