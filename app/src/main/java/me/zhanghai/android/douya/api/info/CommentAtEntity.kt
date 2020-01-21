/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CommentAtEntity(
    val end: Int = 0,
    val start: Int = 0,
    @Json(name = "subject_type")
    val subjectType: String = "",
    val title: String = "",
    val uri: String = ""
)
