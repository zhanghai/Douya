/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.arch

sealed class Stateful<out T>

object Ready : Stateful<Nothing>()

object Loading : Stateful<Nothing>()

data class Success<out T>(val value: T) : Stateful<T>()

data class Error(val exception: Exception) : Stateful<Nothing>()
