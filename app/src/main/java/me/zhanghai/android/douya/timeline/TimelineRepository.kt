/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.timeline

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import me.zhanghai.android.douya.api.app.ApiService
import me.zhanghai.android.douya.api.info.Status
import me.zhanghai.android.douya.api.info.TimelineItem
import me.zhanghai.android.douya.arch.Deleted
import me.zhanghai.android.douya.arch.Error
import me.zhanghai.android.douya.arch.Loading
import me.zhanghai.android.douya.arch.ResourceWithMore
import me.zhanghai.android.douya.arch.Success
import me.zhanghai.android.douya.status.StatusRepository
import timber.log.Timber

object TimelineRepository {
    fun observeHomeTimeline() = callbackFlow<ResourceWithMore<List<TimelineItem>>> {
        var resource = ResourceWithMore<List<TimelineItem>>(Deleted(null), Deleted(null))
        val offer = { newResource: ResourceWithMore<List<TimelineItem>> ->
            resource = newResource
            channel.offer(resource)
        }
        var refresh = suspend {}
        var loadMore = suspend {}

        refresh = refresh@{
            offer(ResourceWithMore(Loading(resource.value.value), Deleted(null)))
            val timeline = try {
                getHomeTimeline()
            } catch (e: Exception) {
                Timber.e(e)
                offer(ResourceWithMore(Error(resource.value.value, e, refresh), Deleted(null)))
                return@refresh
            }
            offer(
                ResourceWithMore(
                    Success(timeline, refresh),
                    if (timeline.isNotEmpty()) Success(null, loadMore) else Deleted(null)
                )
            )
        }

        loadMore = loadMore@{
            offer(resource.copy(more = Loading(null)))
            val timeline = resource.value.value!!
            val moreTimeline = try {
                getHomeTimeline(timeline.last().uid)
            } catch (e: Exception) {
                Timber.e(e)
                offer(resource.copy(more = Error(null, e, loadMore)))
                return@loadMore
            }
            offer(
                ResourceWithMore(
                    Success(timeline + moreTimeline, refresh),
                    if (moreTimeline.isNotEmpty()) Success(null, loadMore) else Deleted(null)
                )
            )
        }

        refresh()

        val statusObserver = statusObserver@{ status: Status ->
            val timeline = resource.value.value ?: return@statusObserver
            var changed = false
            val newTimeline = timeline.map {
                when {
                    it.content?.status?.id == status.id -> {
                        changed = true
                        it.copy(content = it.content.copy(status = status))
                    }
                    it.content?.status?.parentStatus?.id == status.id -> {
                        changed = true
                        it.copy(
                            content = it.content.copy(
                                status = it.content.status.copy(
                                    parentStatus = status
                                )
                            )
                        )
                    }
                    it.content?.status?.resharedStatus?.id == status.id -> {
                        changed = true
                        it.copy(
                            content = it.content.copy(
                                status = it.content.status.copy(
                                    resharedStatus = status
                                )
                            )
                        )
                    }
                    else -> it
                }
            }
            if (changed) {
                offer(resource.copy(value = Success(newTimeline, refresh)))
            }
        }
        StatusRepository.addObserver(statusObserver)

        awaitClose { StatusRepository.removeObserver(statusObserver) }
    }

    private suspend fun getHomeTimeline(maxId: String? = null) =
        ApiService.getHomeTimeline(maxId).items.filter { it.type.isNotEmpty() }.also {
            it.forEach { it.content?.status?.let { StatusRepository.putStatus(it) } }
        }
}
