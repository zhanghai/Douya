/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.arch

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class ResumedLifecycleOwner : LifecycleOwner {

    private val lifecycle = LifecycleRegistry(this)

    init {
        lifecycle.currentState = Lifecycle.State.RESUMED
    }

    override fun getLifecycle(): Lifecycle = lifecycle
}
