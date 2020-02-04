/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StatusGalleryTopic(
    //@Json(name = "ad_monitor_urls")
    //val adMonitorUrls: List<String> = emptyList(),
    @Json(name = "card_subtitle")
    val cardSubtitle: String = "",
    //@Json(name = "click_track_urls")
    //val clickTrackUrl: List<String> = emptyList(),
    @Json(name = "content_type")
    val contentType: Int = 0,
    @Json(name = "cover_url")
    val coverPic: String = "",
    @Json(name = "creator")
    val creator: UserAbstract? = null,
    val dataType: Int = 0,
    @Json(name = "guest_only")
    val guestOnly: Boolean = false,
    val guests: List<StatusGuest> = emptyList(),
    val id: String = "",
    val introduction: String = "",
    @Json(name = "is_ad")
    val isAd: Boolean = false,
    @Json(name = "is_subscribed")
    val isSubscribed: Boolean = false,
    val label: String = "",
    val name: String = "",
    @Json(name = "participant_count")
    val participantCount: Int = 0,
    @Json(name = "post_count")
    val postCount: Int = 0,
    val reason: String = "",
    @Json(name = "sharing_url")
    val sharingUrl: String = "",
    @Json(name = "subscription_count")
    val subscriptionCount: Int = 0,
    @Json(name = "topic_icon")
    val topicIcon: String = "",
    @Json(name = "topic_icon_large")
    val topicIconLarge: String = "",
    @Json(name = "topic_label_bg_color")
    val topicLabelBgColor: String = "",
    @Json(name = "topic_label_bg_img")
    val topicLabelBgImg: String = "",
    @Json(name = "topic_label_hashtag_color")
    val topicLabelHashtagColor: String = "",
    @Json(name = "topic_label_text_color")
    val topicLabelTextColor: String = "",
    @Json(name = "tail_icon")
    val topicTail: TopicTail? = null,
    val uri: String = "",
    val url: String = ""
)
