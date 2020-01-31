/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.status

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LiveData
import coil.api.load
import coil.transform.CircleCropTransformation
import me.zhanghai.android.douya.R
import me.zhanghai.android.douya.api.info.Status
import me.zhanghai.android.douya.arch.DistinctMutableLiveData
import me.zhanghai.android.douya.arch.ResumedLifecycleOwner
import me.zhanghai.android.douya.databinding.StatusLayoutBinding
import me.zhanghai.android.douya.util.layoutInflater

class StatusLayout : ConstraintLayout {

    private val binding = StatusLayoutBinding.inflate(context.layoutInflater, this, true)

    val viewModel = ViewModel()

    init {
        binding.lifecycleOwner = ResumedLifecycleOwner()
        binding.viewModel = viewModel

        viewModel.avatar.observeForever {
            binding.avatarImage.load(it) {
                placeholder(R.drawable.avatar_placeholder)
                transformations(CircleCropTransformation())
            }
        }
    }

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

    fun bind(status: Status) = viewModel.bind(status)

    class ViewModel {

        private val _avatar = DistinctMutableLiveData<String>()
        val avatar: LiveData<String> = _avatar

        private val _author = DistinctMutableLiveData<String>()
        val author: LiveData<String> = _author

        private val _time = DistinctMutableLiveData<String>()
        val time: LiveData<String> = _time

        private val _activity = DistinctMutableLiveData<String>()
        val activity: LiveData<String> = _activity

        private val _text = DistinctMutableLiveData<String>()
        val text: LiveData<String> = _text

        fun bind(status: Status) {
            _avatar.value = status.author.avatar
            _author.value = status.author.name
            _activity.value = status.activity
            _time.value = status.createTime
            _text.value = status.text
        }
    }
}
