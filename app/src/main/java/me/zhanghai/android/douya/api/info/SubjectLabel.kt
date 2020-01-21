/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SubjectLabel(
    val icon: String = "",
    val id: String = "",
    val title: String = "",
    val uri: String = ""
)
