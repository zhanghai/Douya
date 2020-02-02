/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.arch

import androidx.lifecycle.MutableLiveData

open class MutableLiveData<T> : MutableLiveData<T> {
    constructor()

    constructor(value: T) : super(value)

    @Suppress("UNCHECKED_CAST")
    override fun getValue(): T = super.getValue() as T
}
