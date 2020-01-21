/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SizedPhoto(
    val description: String = "",
    val id: String = "",
    @Json(name = "image")
    val images: SizedImage? = null,
    @Json(name = "tag_name")
    val tag: String = ""
)
