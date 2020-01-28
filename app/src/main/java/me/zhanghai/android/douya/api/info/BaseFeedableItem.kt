/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BaseFeedableItem(
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
    override val uri: String = ""
) : IBaseFeedableItem
