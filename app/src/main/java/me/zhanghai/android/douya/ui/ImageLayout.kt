/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.view.doOnAttach
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.MutableLiveData
import me.zhanghai.android.douya.R
import me.zhanghai.android.douya.api.info.SizedImage
import me.zhanghai.android.douya.api.info.VideoInfo
import me.zhanghai.android.douya.api.util.normalOrClosest
import me.zhanghai.android.douya.arch.ResumedLifecycleOwner
import me.zhanghai.android.douya.arch.mapDistinct
import me.zhanghai.android.douya.databinding.ImageLayoutBinding
import me.zhanghai.android.douya.util.getDrawableByAttr
import me.zhanghai.android.douya.util.layoutInflater

class ImageLayout : FrameLayout {
    private val lifecycleOwner = ResumedLifecycleOwner()

    private val binding = ImageLayoutBinding.inflate(context.layoutInflater, this, true)

    private val viewModel = ViewModel()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        foreground = context.getDrawableByAttr(R.attr.selectableItemBackground)
        setOnClickListener { viewModel.open() }
        doOnAttach {
            val layoutParams = layoutParams
            binding.imageImage.updateLayoutParams {
                width = layoutParams.width
                height = layoutParams.height
            }
        }

        binding.lifecycleOwner = lifecycleOwner
        binding.viewModel = viewModel
    }

    fun setImage(image: SizedImage?) {
        viewModel.setImage(image)
        binding.executePendingBindings()
    }

    fun setVideo(video: VideoInfo?) {
        viewModel.setVideo(video)
        binding.executePendingBindings()
    }

    fun setImageOrVideo(imageOrVideo: Any?) {
        when (imageOrVideo) {
            is SizedImage -> setImage(imageOrVideo)
            is VideoInfo -> setVideo(imageOrVideo)
            else -> setImage(null)
        }
    }

    class ViewModel {
        data class State(
            val ratio: Float,
            val url: String,
            val isGif: Boolean,
            val isVideo: Boolean
        )

        private val state = MutableLiveData(
            State(
                ratio = 1f,
                url = "",
                isGif = false,
                isVideo = false
            )
        )
        val ratio = state.mapDistinct { it.ratio }
        val url = state.mapDistinct { it.url }
        val isGif = state.mapDistinct { it.isGif }
        val isVideo = state.mapDistinct { it.isVideo }

        fun setImage(image: SizedImage?) {
            val imageItem = image?.normalOrClosest
            state.value = State(
                ratio = imageItem?.let { it.width.toFloat() / it.height } ?: 1f,
                url = imageItem?.url ?: "",
                isGif = image?.isAnimated ?: false,
                isVideo = false
            )
        }

        fun setVideo(video: VideoInfo?) {
            state.value = State(
                ratio = video?.let { it.videoWidth.toFloat() / it.videoHeight } ?: 1f,
                url = video?.coverUrl ?: "",
                isGif = false,
                isVideo = true
            )
        }

        fun open() {
            // TODO
        }
    }
}
