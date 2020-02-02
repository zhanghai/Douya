/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LiveData
import me.zhanghai.android.douya.api.info.SizedImage
import me.zhanghai.android.douya.api.util.normalOrClosest
import me.zhanghai.android.douya.arch.DistinctMutableLiveData
import me.zhanghai.android.douya.arch.ResumedLifecycleOwner
import me.zhanghai.android.douya.databinding.ImageLayoutBinding
import me.zhanghai.android.douya.util.layoutInflater

class ImageLayout : FrameLayout {

    private val binding = ImageLayoutBinding.inflate(context.layoutInflater, this, true)

    val viewModel = ViewModel()

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
        binding.lifecycleOwner = ResumedLifecycleOwner()
        binding.viewModel = viewModel
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        val layoutParams = layoutParams
        binding.imageImage.updateLayoutParams {
            width = layoutParams.width
            height = layoutParams.height
        }
    }

    fun bind(image: SizedImage?) {
        viewModel.bind(image)
        binding.executePendingBindings()
    }

    inner class ViewModel {

        private val _ratio = DistinctMutableLiveData(1f)
        val ratio: LiveData<Float> = _ratio

        private val _image = DistinctMutableLiveData("")
        val image: LiveData<String> = _image

        private val _isGif = DistinctMutableLiveData(false)
        val isGif: LiveData<Boolean> = _isGif

        fun bind(image: SizedImage?) {
            val imageItem = image?.normalOrClosest
            _ratio.value = imageItem?.let { it.width.toFloat() / it.height } ?: 1f
            _image.value = imageItem?.url ?: ""
            _isGif.value = image?.isAnimated ?: false
        }

        fun open() {
            // TODO
        }
    }
}
