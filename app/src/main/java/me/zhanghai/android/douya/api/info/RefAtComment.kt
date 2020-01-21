/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RefAtComment(
    val entities: List<CommentAtEntity> = emptyList(),
    @Json(name = "has_ref")
    val hasRef: Boolean = false,
    @Json(name = "is_voted")
    val isVoted: Boolean = false,
    val position: Int = 0,
    @Json(name = "ref_comment")
    val refComment: Comment? = null,
    val replies: List<RefAtComment> = emptyList(),
    @Json(name = "total_replies")
    val totalReplies: Int = 0,
    val type: Int = 0,
    @Json(name = "vote_count")
    val voteCount: Int = 0
)
