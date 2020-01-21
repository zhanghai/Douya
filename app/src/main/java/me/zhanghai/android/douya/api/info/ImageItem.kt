/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImageItem(
    val height: Int = 0,
    val size: Long = 0,
    val url: String = "",
    val width: Int = 0
)
