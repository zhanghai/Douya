/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.status

import me.zhanghai.android.douya.api.info.Status
import java.lang.ref.WeakReference

object StatusRepository {
    private val statuses = mutableMapOf<String, WeakReference<Status>>()
    private val observers = mutableSetOf<(Status) -> Unit>()

    fun putStatus(status: Status) {
        statuses[status.id] = WeakReference(status)
        observers.forEach { it(status) }
    }

    fun addObserver(observer: (Status) -> Unit) {
        observers.add(observer)
    }

    fun removeObserver(observer: (Status) -> Unit) {
        observers.remove(observer)
    }
}
