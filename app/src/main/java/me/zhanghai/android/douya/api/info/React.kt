/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class React(
    @Json(name = "reaction_type")
    val reactionType: ReactionType? = null,
    val text: String = "",
    val time: String = "",
    val user: UserAbstract? = null
) {
    enum class ReactionType {
        @Json(name = "0")
        CANCEL_VOTE,
        @Json(name = "1")
        VOTE
    }
}
