/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImageBlock(
    val count: Int = 0,
    @Json(name = "count_str")
    val countStr: String = "",
    val images: List<StatusCardImage> = emptyList()
)
