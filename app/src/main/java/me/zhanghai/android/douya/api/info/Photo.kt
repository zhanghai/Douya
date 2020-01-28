/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Photo(
    // BaseFeedableItem
    @Json(name = "abstract")
    override val abstractString: String = "",
    @Json(name = "url")
    override val alt: String = "",
    @Json(name = "cover_url")
    override val coverUrl: String = "",
    override val id: String = "",
    @Json(name = "sharing_url")
    override val sharingUrl: String = "",
    override val title: String = "",
    override val type: String = "",
    override val uri: String = "",

    val author: UserAbstract? = null,
    @Json(name = "collections_count")
    val collectionsCount: Int = 0,
    @Json(name = "comments_count")
    val commentsCount: Int = 0,
    @Json(name = "create_time")
    val createTime: String = "",
    val description: String = "",
    @Json(name = "donate_count")
    val donateCount: Int = 0,
    @Json(name = "allow_donate")
    val enableDonate: Boolean = false,
    val image: SizedImage? = null,
    @Json(name = "is_collected")
    val isCollected: Boolean = false,
    @Json(name = "is_original")
    val isOriginal: Boolean = false,
    @Json(name = "latest_comments")
    val latestComments: List<Comment> = emptyList(),
    val liked: Boolean = false,
    @Json(name = "likers_count")
    val likersCount: Int = 0,
    @Json(name = "allow_comment")
    val allowComment: Boolean = false,
    val owner: BaseFeedableItem? = null,
    @Json(name = "owner_uri")
    val ownerUri: String = "",
    val position: Int = 0,
    @Json(name = "reaction_type")
    val reactionType: Int = 0,
    @Json(name = "reactions_count")
    val reactionsCount: Int = 0,
    @Json(name = "read_count")
    val readCount: Int = 0,
    @Json(name = "reply_limit")
    val replyLimit: String = "",
    @Json(name = "reshares_count")
    val resharesCount: Int = 0,
    val status: Status? = null
) : IBaseFeedableItem
