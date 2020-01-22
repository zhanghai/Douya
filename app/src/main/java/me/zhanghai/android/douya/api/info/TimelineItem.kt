/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import me.zhanghai.android.douya.network.EmptyObjectToNull

@JsonClass(generateAdapter = true)
data class TimelineItem(
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

    val action: String = "",
    //@Json(name = "ad_info")
    //val adInfo: FeedAd? = null,
    val comments: List<RefAtComment> = emptyList(),
    @Json(name = "comments_count")
    val commentsCount: Int = 0,
    @EmptyObjectToNull
    val content: CommonContent? = null,
    @Json(name = "created_time")
    val createTime: String = "",
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
    val reactionType: Int = 0,
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
) : BaseFeedableItem {

    companion object {

        const val LAYOUT_AD_CONTENT = 15
        const val LAYOUT_AD_FOUR_IMAGES = 21
        const val LAYOUT_AD_ONE_LARGE_IMAGE = 6
        const val LAYOUT_AD_VIDEO = 5
        const val LAYOUT_ALBUM = 3
        const val LAYOUT_ALBUM_DEFAULT = 11
        const val LAYOUT_BANNER = 17
        const val LAYOUT_DEFAULT_CONTENT_RECTANGLE = 2
        const val LAYOUT_DEFAULT_CONTENT_SQUARE = 2
        const val LAYOUT_FOLD_PHOTO = 4
        const val LAYOUT_NAV_TAB = 13
        const val LAYOUT_ONE_LARGE_IMAGE = 9
        const val LAYOUT_RECOMMEND_GROUPS_LAYOUT = 12
        const val LAYOUT_RECOMMEND_TOPICS = 7
        const val LAYOUT_STATUS = 1
        const val LAYOUT_SUBJECT_LAYOUT = 10
        const val LAYOUT_TOPIC_CARD = 16
        const val LAYOUT_VIDEO_DEFAULT = 8
    }
}
