/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StatusCardImage(
    val image: SizedImage? = null,
    val uri: String = ""
)
