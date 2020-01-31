/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.arch

class DistinctMutableLiveData<T> : MutableLiveData<T> {
    constructor() : super()

    constructor(value: T) : super(value)

    override fun setValue(value: T) {
        if (getValue() != value) {
            super.setValue(value)
        }
    }

    fun forceValue(value: T) = super.setValue(value)
}
