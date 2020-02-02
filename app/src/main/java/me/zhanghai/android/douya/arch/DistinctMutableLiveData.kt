/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.arch

class DistinctMutableLiveData<T>(value: T) : MutableLiveData<T>(value) {

    override fun setValue(value: T) {
        if (getValue() != value) {
            super.setValue(value)
        }
    }

    fun forceValue(value: T) {
        super.setValue(value)
    }
}
