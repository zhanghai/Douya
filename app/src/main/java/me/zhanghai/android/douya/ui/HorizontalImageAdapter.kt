/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.zhanghai.android.douya.api.info.SizedImage
import me.zhanghai.android.douya.api.util.rawOrClosest
import me.zhanghai.android.douya.databinding.HorizontalImageItemBinding
import me.zhanghai.android.douya.util.layoutInflater

class HorizontalImageAdapter
    : ListAdapter<SizedImage, HorizontalImageAdapter.ViewHolder>(DIFF_CALLBACK) {
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SizedImage>() {
            override fun areItemsTheSame(oldItem: SizedImage, newItem: SizedImage): Boolean =
                oldItem.rawOrClosest == newItem.rawOrClosest

            override fun areContentsTheSame(oldItem: SizedImage, newItem: SizedImage): Boolean =
                oldItem == newItem
        }
    }

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(HorizontalImageItemBinding.inflate(parent.context.layoutInflater, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: HorizontalImageItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: SizedImage) {
            binding.imageLayout.setImage(image)
        }
    }
}
