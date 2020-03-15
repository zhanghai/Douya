/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.arch

data class ResourceWithMore<T>(
    val value: Resource<T>,
    val more: Resource<Nothing>
)
