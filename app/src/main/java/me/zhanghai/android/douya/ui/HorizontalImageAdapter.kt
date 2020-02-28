/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui

import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import me.zhanghai.android.douya.api.info.SizedImage
import me.zhanghai.android.douya.api.util.rawOrClosest
import me.zhanghai.android.douya.arch.ResumedLifecycleOwner
import me.zhanghai.android.douya.arch.mapDistinct
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
        holder.setImage(getItem(position))
    }

    class ViewHolder(
        private val binding: HorizontalImageItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val lifecycleOwner = ResumedLifecycleOwner()

        private val viewModel = ViewModel()

        init {
            binding.lifecycleOwner = lifecycleOwner
            binding.viewModel = viewModel
        }

        fun setImage(image: SizedImage?) {
            viewModel.setImage(image)
            binding.executePendingBindings()
        }

        class ViewModel {
            data class State(
                val image: SizedImage?
            ) {
                companion object {
                    val INITIAL = State(
                        image = null
                    )
                }
            }

            private val state = MutableLiveData(State.INITIAL)

            val image = state.mapDistinct { it.image }

            fun setImage(image: SizedImage?) {
                state.value = if (image != null) {
                    State(
                        image = image
                    )
                } else {
                    State.INITIAL
                }
            }
        }
    }
}
