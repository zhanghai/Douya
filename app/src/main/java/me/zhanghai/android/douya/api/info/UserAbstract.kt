/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserAbstract(
    @Json(name = "abstract")
    override val abstractIntro: String = "",
    override val avatar: String = "",
    override val birthday: String = "",
    @Json(name = "follow_source")
    override val followSource: String = "",
    override val gender: String = "",
    override val id: String = "",
    override val intro: String = "",
    @Json(name = "large_avatar")
    override val largeAvatar: String = "",
    override val name: String = "",
    @Json(name = "sharing_url")
    override val shareUri: String = "",
    @Json(name = "kind")
    override val type: String = "",
    override val uid: String = "",
    override val uri: String = "",
    override val url: String = "",
    @Json(name = "verify_reason")
    override val verifyReason: String = "",
    @Json(name = "verify_type")
    override val verifyType: Int = 0
) : IUserAbstract
