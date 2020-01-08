/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.arch

import androidx.lifecycle.MutableLiveData

open class MutableLiveData<T> : MutableLiveData<T> {

    constructor(value: T) : super(value)

    protected constructor()

    override fun getValue(): T {
        @Suppress("UNCHECKED_CAST")
        return super.getValue() as T
    }

    override fun setValue(value: T) {
        if (getValue() != value) {
            super.setValue(value)
        }
    }

    fun forceValue(value: T) = super.setValue(value)
}
