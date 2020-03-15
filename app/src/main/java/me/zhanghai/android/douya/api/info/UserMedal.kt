/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserMedal(
    val icon: String = "",
    val kind: String = "",
    @Json(name = "kind_name")
    val kindName: String = "",
    val targets: List<UserMedalTarget> = emptyList()
) {
    companion object {
        const val KIND_ARK = "ark_author";
        const val KIND_MUSIC = "music_artist";
        const val KIND_YPY = "ypy_photographer";
    }
}
