/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.arch

sealed class Resource<out T> {
    abstract val value: T?
    abstract val refresh: (suspend () -> Unit)?
}

data class Loading<out T>(
    override val value: T?
) : Resource<T>() {
    override val refresh = null
}

data class Success<out T>(
    override val value: T?,
    override val refresh: suspend () -> Unit
) : Resource<T>()

data class Error<out T>(
    override val value: T?,
    val exception: Exception,
    override val refresh: suspend () -> Unit
) : Resource<T>()

data class Deleted<out T>(
    override val value: T?
) : Resource<T>() {
    override val refresh = null
}
