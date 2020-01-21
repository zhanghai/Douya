/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MovieHonor(
    val kind: String = "",
    val rank: Int = 0,
    val title: String = "",
    val uri: String = ""
)
