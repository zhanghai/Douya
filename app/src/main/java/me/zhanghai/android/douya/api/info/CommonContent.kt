/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CommonContent(
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
    val photos: List<Photo>? = emptyList(),
    @Json(name = "photos_count")
    val photosCount: Int = 0,
    val status: Status? = null,
    @Json(name = "status_str")
    val statusStr: String = "",
    val subject: LegacySubject? = null,
    val text: String = "",
    @Json(name = "video_info")
    val videoInfo: VideoInfo? = null
)
