/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.arch

sealed class Resource<T> {
    abstract val value: T?
    abstract val refresh: (suspend () -> Unit)?

    fun copyWithValue(value: T?): Resource<T> =
        when (this) {
            is Loading -> copy(value = value)
            is Success -> copy(value = value)
            is Error -> copy(value = value)
            is Deleted -> copy(value = value)
        }
}

data class Loading<T>(
    override val value: T?
) : Resource<T>() {
    override val refresh = null
}

data class Success<T>(
    override val value: T?,
    override val refresh: suspend () -> Unit
) : Resource<T>()

data class Error<T>(
    override val value: T?,
    val exception: Exception,
    override val refresh: suspend () -> Unit
) : Resource<T>()

data class Deleted<T>(
    override val value: T?
) : Resource<T>() {
    override val refresh = null
}
