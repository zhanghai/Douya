/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class InterestInfo(
    @Json(name = "create_time")
    val createTime: String = "",
    val rating: Rating? = null,
    val status: String = "",
    @Json(name = "subject_id")
    val subjectId: String = "",
    @Json(name = "subject_type")
    val subjectType: String = "",
    @Json(name = "wish_count")
    val wishCount: Int = 0
)
