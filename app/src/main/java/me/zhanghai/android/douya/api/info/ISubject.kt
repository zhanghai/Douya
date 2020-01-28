/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.info

interface ISubject : IBaseFeedableItem {
    val cardSubtitle: String
    val colorScheme: ColorScheme?
    val hasLinewatch: Boolean
    val headInfo: HeadInfo?
    val inBlackList: Boolean
    val isRestrictive: Boolean
    val restrictiveIconUrl: String
    val subType: String
    val tags: List<Tag>
}
