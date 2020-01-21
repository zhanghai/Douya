/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

interface BaseFeedableItem {
    val abstractString: String
    val alt: String
    val coverUrl: String
    val id: String
    val sharingUrl: String
    val title: String
    val type: String
    val uri: String
}
