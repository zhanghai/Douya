/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

interface IComment {
    val author: UserAbstract?
    val censorMessage: String
    val createTime: String
    val id: String
    val isCensoring: Boolean
    val isDeleted: Boolean
    val isFolded: Boolean
    val parentCommentId: String
    val photos: List<SizedPhoto>
    val text: String
    val uri: String
}
