/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

import android.content.Context
import androidx.annotation.ArrayRes
import androidx.annotation.BoolRes
import androidx.annotation.DimenRes
import androidx.annotation.IntegerRes
import androidx.core.content.res.ResourcesCompat

fun Context.getBoolean(@BoolRes id: Int) = resources.getBoolean(id)

fun Context.getFloat(@DimenRes id: Int) = ResourcesCompat.getFloat(resources, id)

fun Context.getInteger(@IntegerRes id: Int) = resources.getInteger(id)

fun Context.getStringArray(@ArrayRes id: Int): Array<String> = resources.getStringArray(id)
