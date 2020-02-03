/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.arch

import androidx.lifecycle.MutableLiveData

class DistinctMutableLiveData<T>(value: T) : MutableLiveData<T>(value) {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(): T = super.getValue() as T

    override fun setValue(value: T) {
        if (this.value != value) {
            super.setValue(value)
        }
    }

    fun forceValue(value: T) {
        super.setValue(value)
    }
}
