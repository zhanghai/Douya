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
    val abstractIntro: String = "",
    val avatar: String = "",
    val birthday: String = "",
    @Json(name = "follow_source")
    val followSource: String = "",
    val gender: String = "",
    val id: String = "",
    val intro: String = "",
    @Json(name = "large_avatar")
    val largeAvatar: String = "",
    val name: String = "",
    @Json(name = "sharing_url")
    val shareUri: String = "",
    @Json(name = "kind")
    val type: String = "",
    val uid: String = "",
    val uri: String = "",
    val url: String = "",
    @Json(name = "verify_reason")
    val verifyReason: String = "",
    @Json(name = "verify_type")
    val verifyType: Int = 0
) {

    companion object {

        const val TYPE_SITE = "site"
        const val TYPE_USER = "user"

        const val VERIFY_TYPE_NONE = 0
        const val VERIFY_TYPE_OFFICIAL = 1
        const val VERIFY_TYPE_PERSONAL = 3
        const val VERIFY_TYPE_THIRD = 2
        const val VERIFY_TYPE_VERIFIED_USER = 4
    }
}
