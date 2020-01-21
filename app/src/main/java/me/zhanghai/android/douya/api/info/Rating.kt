/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Rating(
    val count: Int = 0,
    val max: Int = 0,
    @Json(name = "star_count")
    val starCount: Float = 0f,
    val value: Float = 0f
)
