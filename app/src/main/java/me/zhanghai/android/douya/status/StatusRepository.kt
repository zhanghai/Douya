/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.status

import me.zhanghai.android.douya.api.app.ApiService
import me.zhanghai.android.douya.api.info.React
import me.zhanghai.android.douya.api.info.Status
import me.zhanghai.android.douya.util.takeIfNotEmpty
import java.lang.ref.WeakReference

object StatusRepository {
    private val statuses = mutableMapOf<String, WeakReference<Status>>()
    private val statusIdsByParentId = mutableMapOf<String, MutableSet<String>>()
    private val statusIdsByResharedId = mutableMapOf<String, MutableSet<String>>()
    private val observers = mutableSetOf<(Status) -> Unit>()

    fun putStatus(status: Status) {
        val changed = statuses[status.id]?.get() != status
        statuses[status.id] = WeakReference(status)
        if (changed) {
            observers.forEach { it(status) }
        }
        statusIdsByParentId.remove(status.id)?.mapNotNull { statuses[it]?.get() }
            ?.mapTo(mutableSetOf()) {
                val parentStatusChanged = it.parentStatus != status
                val statusWithNewParentStatus = it.copy(parentStatus = status)
                statuses[it.id] = WeakReference(statusWithNewParentStatus)
                if (parentStatusChanged) {
                    observers.forEach { it(statusWithNewParentStatus) }
                }
                it.id
            }?.takeIfNotEmpty()?.let { statusIdsByParentId[status.id] = it }
        statusIdsByResharedId.remove(status.id)?.mapNotNull { statuses[it]?.get() }
            ?.mapTo(mutableSetOf()) {
                val resharedStatusChanged = it.resharedStatus != status
                val statusWithNewResharedStatus = it.copy(resharedStatus = status)
                statuses[it.id] = WeakReference(statusWithNewResharedStatus)
                if (resharedStatusChanged) {
                    observers.forEach { it(statusWithNewResharedStatus) }
                }
                it.id
            }?.takeIfNotEmpty()?.let { statusIdsByResharedId[status.id] = it }
        if (status.parentStatus != null) {
            statusIdsByParentId.getOrPut(status.parentStatus.id) { mutableSetOf() }.add(status.id)
        }
        if (status.resharedStatus != null) {
            statusIdsByResharedId.getOrPut(status.resharedStatus.id) { mutableSetOf() }
                .add(status.id)
        }
    }

    fun addObserver(observer: (Status) -> Unit) {
        observers.add(observer)
    }

    fun removeObserver(observer: (Status) -> Unit) {
        observers.remove(observer)
    }
}
