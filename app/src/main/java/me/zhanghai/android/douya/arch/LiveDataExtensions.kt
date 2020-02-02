/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.arch

import androidx.lifecycle.LiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map

fun <X, Y> LiveData<X>.mapDistinct(mapFunction: (X) -> Y): LiveData<Y> =
    map(mapFunction).distinctUntilChanged()
