/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ColorScheme(
    @Json(name = "is_dark")
    val isDark: Boolean = false,
    @Json(name = "primary_color_dark")
    val primaryColorDark: String = "",
    @Json(name = "primary_color_light")
    val primaryColorLight: String = "",
    @Json(name = "secondary_color")
    val secondaryColor: String = ""
)
