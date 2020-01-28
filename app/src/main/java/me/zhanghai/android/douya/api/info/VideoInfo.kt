/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VideoInfo(
    @Json(name = "alert_text")
    val alertText: String = "",
    @Json(name = "cover_url")
    val coverUrl: String = "",
    val description: String = "",
    val duration: String = "",
    @Json(name = "file_size")
    val fileSize: Int = 0,
    val id: String = "",
    val label: String = "",
    @Json(name = "play_count")
    val playCount: Int = 0,
    @Json(name = "play_status")
    val playStatus: Int = 0,
    @Json(name = "preview_url")
    val previewUrl: String = "",
    val uri: String = "",
    @Json(name = "video_height")
    val videoHeight: Int = 0,
    @Json(name = "video_url")
    val videoUrl: String = "",
    @Json(name = "video_watermark_url")
    val videoWatermarkUrl: String = "",
    @Json(name = "video_width")
    val videoWidth: Int = 0
) {
    companion object {
        val VIDEO_PLAY_STATUS_BANNED = 2
        val VIDEO_PLAY_STATUS_IN_REVIEW = 0
        val VIDEO_PLAY_STATUS_READY = 1
    }
}
