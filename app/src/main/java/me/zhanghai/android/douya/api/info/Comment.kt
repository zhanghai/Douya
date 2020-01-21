/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Comment(
    val author: UserAbstract? = null,
    @Json(name = "censor_message")
    val censorMessage: String = "",
    @Json(name = "create_time")
    val createTime: String = "",
    val id: String = "",
    @Json(name = "is_censoring")
    val isCensoring: Boolean = false,
    @Json(name = "is_deleted")
    val isDeleted: Boolean = false,
    @Json(name = "is_folded")
    val isFolded: Boolean = false,
    @Json(name = "parent_comment_id")
    val parentCid: String = "",
    val photos: List<SizedPhoto> = emptyList(),
    val text: String = "",
    val uri: String = ""
)
