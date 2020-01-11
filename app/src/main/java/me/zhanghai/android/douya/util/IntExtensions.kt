/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

fun Int.hasBits(bits: Int) = this and bits == bits

infix fun Int.andInv(other: Int) = this and other.inv()
