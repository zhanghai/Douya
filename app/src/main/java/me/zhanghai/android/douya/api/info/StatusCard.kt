/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StatusCard(
    val activity: String = "",
    @Json(name = "additional_info")
    val additionalInfo: String = "",
    @Json(name = "card_style")
    val cardType: String = "",
    @Json(name = "subtitle_entities")
    val entities: List<CommentAtEntity> = emptyList(),
    @Json(name = "has_linewatch")
    val hasLineWatch: Boolean = false,
    @Json(name = "honor_info")
    val honorInfo: HonorInfo? = null,
    @Json(name = "images_block")
    val imageBlock: ImageBlock? = null,
    @Json(name = "image_label")
    val imageLabel: String = "",
    @Json(name = "interest_info")
    val interestInfo: InterestInfo? = null,
    @Json(name = "more_uri")
    val moreUri: String = "",
    @Json(name = "null_rating_reason")
    val nullRatingReason: String = "",
    @Json(name = "obsolete_msg")
    val obsoleteMsg: String = "",
    @Json(name = "owner_name")
    val ownerName: String = "",
    @Json(name = "owner_uri")
    val ownerUri: String = "",
    val rating: Rating? = null,
    val image: SizedImage? = null,
    @Json(name = "subtitle")
    val subTitle: String = "",
    @Json(name = "subject_label")
    val subjectLabel: SubjectLabel? = null,
    val title: String = "",
    val topic: StatusGalleryTopic? = null,
    val uri: String = "",
    val url: String = "",
    @Json(name = "verify_type")
    val verifyType: Int = 0
)
