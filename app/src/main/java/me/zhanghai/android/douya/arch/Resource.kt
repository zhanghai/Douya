/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.arch

sealed class Resource<out T>

object Loading : Resource<Nothing>()

interface Refreshable {
    val refresh: suspend () -> Unit
}

data class Success<out T>(
    val value: T,
    override val refresh: suspend () -> Unit
) : Resource<T>(), Refreshable

data class Error(
    val exception: Exception,
    override val refresh: suspend () -> Unit
) : Resource<Nothing>(), Refreshable

object Deleted : Resource<Nothing>()
