/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProfileImage(
    // IImage
    @Json(name = "is_animated")
    override val isAnimated: Boolean = false,
    override val large: String = "",
    override val normal: String = "",

    val color: String = "",
    @Json(name = "is_default")
    val isDefault: Boolean = false
) : IImage
