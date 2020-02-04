/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

fun <T> Collection<T>.takeIfNotEmpty(): Collection<T>? = if (isNotEmpty()) this else null
