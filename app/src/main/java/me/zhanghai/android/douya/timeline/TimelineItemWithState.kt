/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.timeline

import me.zhanghai.android.douya.api.info.TimelineItem

data class TimelineItemWithState(
    val timelineItem: TimelineItem,
    val isLiking: Boolean = false,
    val isResharing: Boolean = false
)
