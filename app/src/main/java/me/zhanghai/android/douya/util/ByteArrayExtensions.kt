/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

@OptIn(ExperimentalUnsignedTypes::class)
fun ByteArray.toHexString(): String = asUByteArray().toHexString()

@OptIn(ExperimentalUnsignedTypes::class)
fun UByteArray.toHexString(): String = joinToString("") { it.toString(16).padStart(2, '0') }
