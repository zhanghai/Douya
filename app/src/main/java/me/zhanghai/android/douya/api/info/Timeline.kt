/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Timeline(
    val count: Int = 0,
    val items: List<TimelineItem> = emptyList(),
    @Json(name = "new_item_count")
    val newItemCount: Int = 0,
    @Json(name = "next_id")
    val nextId: String = "",
    val start: Int = 0,
    val toast: String = "",
    val total: Int = 0
)
