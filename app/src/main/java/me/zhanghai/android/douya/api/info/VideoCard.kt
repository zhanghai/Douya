/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VideoCard(
    val action: String = "",
    val author: UserAbstract? = null,
    val card: StatusCard? = null,
    val title: String = "",
    val uri: String = "",
    @Json(name = "video_info")
    val videoInfo: VideoInfo? = null
)
