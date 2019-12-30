/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthenticationResponse(
    @Json(name = "access_token")
    val accessToken: String,
    @Json(name = "douban_user_name")
    val userName: String,
    @Json(name = "douban_user_id")
    val userId: Long,
    @Json(name = "expires_in")
    val accessTokenExpiresIn: Long,
    @Json(name = "refresh_token")
    val refreshToken: String
)
