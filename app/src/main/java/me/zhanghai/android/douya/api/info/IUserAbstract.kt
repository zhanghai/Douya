/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

interface IUserAbstract {
    val abstractIntro: String
    val avatar: String
    val birthday: String
    val followSource: String
    val gender: String
    val id: String
    val intro: String
    val largeAvatar: String
    val name: String
    val shareUri: String
    val type: String
    val uid: String
    val uri: String
    val url: String
    val verifyReason: String
    val verifyType: Int

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
