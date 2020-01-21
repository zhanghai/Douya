/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LegacySubject(
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

    // Subject
    @Json(name = "card_subtitle")
    override val cardSubtitle: String = "",
    @Json(name = "color_scheme")
    override val colorScheme: ColorScheme? = null,
    @Json(name = "has_linewatch")
    override val hasLinewatch: Boolean = false,
    @Json(name = "head_info")
    override val headInfo: HeadInfo? = null,
    @Json(name = "in_blacklist")
    override val inBlackList: Boolean = false,
    @Json(name = "is_restrictive")
    override val isRestrictive: Boolean = false,
    @Json(name = "restrictive_icon_url")
    override val restrictiveIconUrl: String = "",
    @Json(name = "subtype")
    override val subType: String = "",
    override val tags: List<Tag> = emptyList(),

    @Json(name = "alg_json")
    val algJson: String = "",
    @Json(name = "alg_recommend")
    val algRecommend: String = "",
    @Json(name = "article_intros")
    val articleIntros: List<ArticleIntro> = emptyList(),
    @Json(name = "body_bg_color")
    val bodyBgColor: String = "",
    @Json(name = "comment_count")
    val commentCount: Int = 0,
    @Json(name = "forum_topic_count")
    val forumTopicCount: Int = 0,
    @Json(name = "gallery_topic_count")
    val galleryTopicCount: Int = 0,
    @Json(name = "has_joined")
    val hasJoined: Boolean = false,
    @Json(name = "has_rated")
    val hasRated: Boolean = false,
    @Json(name = "header_bg_color")
    val headerBgColor: String = "",
    @Json(name = "honor_infos")
    val honorInfos: List<MovieHonor> = emptyList(),
    //val interest: Interest? = null,
    val intro: String = "",
    @Json(name = "intro_abstract")
    val introAbstract: String = "",
    @Json(name = "is_douban_intro")
    val isDoubanIntro: Boolean = false,
    @Json(name = "null_rating_reason")
    val nullRatingReason: String = "",
    @Json(name = "other_version")
    val otherVersion: OtherVersion? = null,
    @Json(name = "pic")
    val picture: Image? = null,
    val rating: Rating? = null,
    val series: LegacySubjectSeries? = null,
    val tips: List<String> = emptyList(),
    @Json(name = "review_count")
    val totalReviews: Int = 0,
    @Json(name = "ugc_tabs")
    val ugcTabs: List<UgcTab> = emptyList(),
    @Json(name = "vendor_count")
    val vendorCount: Int = 0
) : Subject
