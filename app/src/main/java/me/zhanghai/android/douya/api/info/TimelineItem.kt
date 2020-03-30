/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import me.zhanghai.android.douya.network.EmptyObjectToNull
import org.threeten.bp.ZonedDateTime

@JsonClass(generateAdapter = true)
data class TimelineItem(
    // BaseFeedableItem
    @Json(name = "abstract")
    override val abstractString: String = "",
    @Json(name = "cover_url")
    override val coverUrl: String = "",
    override val id: String = "",
    @Json(name = "sharing_url")
    override val sharingUrl: String = "",
    override val title: String = "",
    override val type: String = "",
    override val uri: String = "",
    override val url: String = "",

    val action: String = "",
    //@Json(name = "ad_info")
    //val adInfo: FeedAd? = null,
    val comments: List<RefAtComment> = emptyList(),
    @Json(name = "comments_count")
    val commentsCount: Int = 0,
    @EmptyObjectToNull
    val content: CommonContent? = null,
    @Json(name = "created_time")
    val createTime: ZonedDateTime?,
    @Json(name = "fold_key")
    val foldKey: String = "",
    val layout: Int = 0,
    @Json(name = "more_item_count")
    val moreItemCount: Int = 0,
    //val notifications: TimelineNotifications? = null,
    val owner: Owner? = null,
    @Json(name = "owner_alter_label")
    val ownerAlterLabel: OwnerAlterLabel? = null,
    val rating: Rating? = null,
    @Json(name = "reaction_type")
    val reactionType: React.ReactionType? = null,
    @Json(name = "reactions_count")
    val reactionsCount: Int = 0,
    //@Json(name = "rec_info")
    //val recInfo: RecInfo? = null,
    val resharer: UserAbstract? = null,
    @Json(name = "reshares_count")
    val resharesCount: Int = 0,
    @Json(name = "show_actions")
    val showActions: Boolean = false,
    @Json(name = "subject_card")
    val subjectCard: StatusCard? = null,
    @Json(name = "subject_label")
    val subjectLabel: SubjectLabel? = null,
    val topic: StatusGalleryTopic? = null,
    //val topics: RecommendTopics? = null,
    @Json(name = "type_cn")
    val typeCn: String = "",
    val uid: String = ""
) : IBaseFeedableItem
