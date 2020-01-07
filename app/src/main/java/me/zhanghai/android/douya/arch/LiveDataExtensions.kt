/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.arch

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations

fun <T> LiveData<T>.observe(owner: LifecycleOwner, observer: (T) -> Unit) {
    observe(owner, Observer { observer(it) })
}

fun <X, Y> LiveData<X>.map(mapFunction: (X) -> Y) = Transformations.map(this, mapFunction)

fun <X, Y> LiveData<X>.switchMap(switchMapFunction: (X) -> LiveData<Y>) =
    Transformations.switchMap(this, switchMapFunction)

fun <X> LiveData<X>.distinctUntilChanged() = Transformations.distinctUntilChanged(this)
