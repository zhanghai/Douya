/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Status(
    val activity: String = "",
    //@Json(name = "ad_info")
    //val adInfo: StatusAdInfo? = null,
    @Json(name = "allow_comment")
    val allowComment: Boolean = false,
    val author: UserAbstract? = null,
    @Json(name = "can_transfer_accessible")
    val canTransferAccessible: Boolean = false,
    @Json(name = "can_transfer_reply_limit")
    val canTransferReplyLimit: Boolean = false,
    val card: StatusCard? = null,
    @Json(name = "censor_info")
    val censorInfo: CensorInfo? = null,
    @Json(name = "collections_count")
    val collectionsCount: Int = 0,
    @Json(name = "comments_count")
    val commentsCount: Int = 0,
    @Json(name = "create_time")
    val createTime: String = "",
    val deleted: Boolean = false,
    val entities: List<CommentAtEntity> = emptyList(),
    @Json(name = "forbid_reshare_and_comment")
    val forbidAdReshareAndComment: Boolean = false,
    @Json(name = "has_related_contents")
    val hasRelatedContent: Boolean = false,
    val hidden: Boolean = false,
    val id: String = "",
    val images: List<SizedImage> = emptyList(),
    @Json(name = "is_collected")
    val isCollected: Boolean = false,
    @Json(name = "is_status_ad")
    val isStatusAd: Boolean = false,
    @Json(name = "is_subscription")
    val isSubscription: Boolean = false,
    @Json(name = "like_count")
    val likeCount: Int = 0,
    val liked: Boolean = false,
    val msg: String = "",
    @Json(name = "parent_status")
    //val parentStatus: ReshareStatus? = null,
    val parentStatus: Status? = null,
    @Json(name = "is_private")
    val privateStatus: Boolean = false,
    @Json(name = "reaction_type")
    val reactionType: Int = 0,
    @Json(name = "reactions_count")
    val reactionsCount: Int = 0,
    val recInfoSource: String = "",
    @Json(name = "reply_limit")
    val replyLimit: String = "",
    @Json(name = "reshare_id")
    val reshareId: String = "",
    @Json(name = "reshared_status")
    val resharedStatus: Status? = null,
    @Json(name = "reshares_count")
    val resharesCount: Int = 0,
    @Json(name = "sharing_url")
    val sharingUrl: String = "",
    @Json(name = "subscription_text")
    val subscriptionText: String = "",
    val text: String = "",
    val topic: StatusGalleryTopic? = null,
    val uri: String = "",
    @Json(name = "video_card")
    val videoCard: VideoCard? = null,
    @Json(name = "video_info")
    val videoInfo: VideoInfo? = null,

    // ReshareStatus
    @Json(name = "parent_id")
    val parentId: String = ""
)
