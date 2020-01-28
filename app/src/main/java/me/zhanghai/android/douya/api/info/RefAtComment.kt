/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RefAtComment(
    // Comment
    override val author: UserAbstract? = null,
    @Json(name = "censor_message")
    override val censorMessage: String = "",
    @Json(name = "create_time")
    override val createTime: String = "",
    override val id: String = "",
    @Json(name = "is_censoring")
    override val isCensoring: Boolean = false,
    @Json(name = "is_deleted")
    override val isDeleted: Boolean = false,
    @Json(name = "is_folded")
    override val isFolded: Boolean = false,
    @Json(name = "parent_comment_id")
    override val parentCommentId: String = "",
    override val photos: List<SizedPhoto> = emptyList(),
    override val text: String = "",
    override val uri: String = "",

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
) : IComment
