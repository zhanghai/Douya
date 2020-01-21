/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Tag(
    val desc: String = "",
    val icon: String = "",
    val id: String = "",
    @Json(name = "is_channel")
    val isChannel: Boolean = false,
    @Json(name = "is_follow")
    val isFollowed: Boolean = false,
    val name: String = "",
    val uri: String = "",
    val url: String = ""
)
