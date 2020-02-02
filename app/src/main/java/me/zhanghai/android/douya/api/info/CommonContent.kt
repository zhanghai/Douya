/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CommonContent(
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

    @Json(name = "album_privacy")
    val albumPrivacy: String = "",
    @Json(name = "alter_author_string")
    val alterAuthorStr: String = "",
    val author: UserAbstract? = null,
    val card: StatusCard? = null,
    @Json(name = "is_private")
    val isPrivate: Boolean = false,
    @Json(name = "more_photos_count")
    val morePhotoCount: Int = 0,
    @Json(name = "more_photos_uri")
    val morePhotosUri: String = "",
    val photo: Photo? = null,
    val photos: List<Photo> = emptyList(),
    @Json(name = "photos_count")
    val photosCount: Int = 0,
    val status: Status? = null,
    @Json(name = "status_str")
    val statusStr: String = "",
    val subject: LegacySubject? = null,
    val text: String = "",
    @Json(name = "video_info")
    val videoInfo: VideoInfo? = null
) : IBaseFeedableItem
