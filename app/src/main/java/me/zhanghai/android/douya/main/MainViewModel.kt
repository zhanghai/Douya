/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import me.zhanghai.android.douya.arch.EventLiveData

class MainViewModel : ViewModel() {
    private val _sendEvent = EventLiveData<Unit>()
    val sendEvent: LiveData<Unit> = _sendEvent

    fun send() {
        _sendEvent.value = Unit
    }
}
