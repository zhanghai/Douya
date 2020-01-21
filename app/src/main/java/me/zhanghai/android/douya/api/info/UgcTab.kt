/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UgcTab(
    val count: Int = 0,
    val id: String = "",
    val source: String = "",
    val title: String = "",
    val type: String = "",
    val uri: String = ""
)
