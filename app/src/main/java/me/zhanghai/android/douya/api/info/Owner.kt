/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Owner(
    @Json(name = "avatar")
    val avatar: String = "",
    @Json(name = "event_label")
    val eventLabel: String = "",
    val id: String = "",
    @Json(name = "is_rect_avatar")
    val isRect: Boolean = false,
    val name: String = "",
    val type: String = "",
    val uri: String = "",
    val url: String = "",
    @Json(name = "verify_type")
    val verifyType: Int = 0
)
