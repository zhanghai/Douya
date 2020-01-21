/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SizedImage(
    @Json(name = "is_animated")
    val isAnimated: Boolean = false,
    val large: ImageItem? = null,
    val normal: ImageItem? = null,
    val raw: ImageItem? = null,
    val small: ImageItem? = null
)
