/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.zhanghai.android.douya.api.app.apiMessage
import me.zhanghai.android.douya.api.info.TimelineItem
import me.zhanghai.android.douya.arch.DistinctMutableLiveData
import me.zhanghai.android.douya.arch.Error
import me.zhanghai.android.douya.arch.EventLiveData
import me.zhanghai.android.douya.arch.Loading
import me.zhanghai.android.douya.arch.ResourceWithMore
import me.zhanghai.android.douya.arch.mapDistinct
import me.zhanghai.android.douya.util.takeIfNotEmpty

class MainViewModel : ViewModel() {
    private val _sendEvent = EventLiveData<Unit>()
    val sendEvent: LiveData<Unit> = _sendEvent

    fun send() {
        _sendEvent.value = Unit
    }
}
