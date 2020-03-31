/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.timeline

import android.net.Uri
import com.squareup.moshi.Types
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import me.zhanghai.android.douya.account.app.activeAccount
import me.zhanghai.android.douya.api.app.ApiService
import me.zhanghai.android.douya.api.info.React
import me.zhanghai.android.douya.api.info.Status
import me.zhanghai.android.douya.api.info.TimelineItem
import me.zhanghai.android.douya.app.accountManager
import me.zhanghai.android.douya.arch.Deleted
import me.zhanghai.android.douya.arch.Error
import me.zhanghai.android.douya.arch.Loading
import me.zhanghai.android.douya.arch.ResourceWithMore
import me.zhanghai.android.douya.arch.Success
import me.zhanghai.android.douya.status.StatusRepository
import me.zhanghai.android.douya.util.JsonDiskCache
import timber.log.Timber
import java.lang.ref.WeakReference

object TimelineRepository {
    private val HOME_TIMELINE_CACHE_TYPE =
        Types.newParameterizedType(List::class.java, TimelineItem::class.java)

    private val timelineItems = mutableMapOf<String, WeakReference<TimelineItemWithState>>()
    private val observers = mutableSetOf<(TimelineItemWithState) -> Unit>()

    fun observeTimeline(userId: String?): Flow<ResourceWithMore<List<TimelineItemWithState>>> =
        callbackFlow {
            var resource = ResourceWithMore<List<TimelineItemWithState>>(
                Deleted(null), Deleted(null)
            )
            val offer = { newResource: ResourceWithMore<List<TimelineItemWithState>> ->
                resource = newResource
                channel.offer(resource)
            }
            var refresh = suspend {}
            var loadMore = suspend {}

            refresh = refresh@{
                offer(ResourceWithMore(Loading(resource.value.value), Deleted(null)))
                val timeline = try {
                    fetchTimeline(userId)
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
                    fetchTimeline(userId, timeline.last().timelineItem.uid)
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

            val observer = observer@{ timelineItem: TimelineItemWithState ->
                val timeline = resource.value.value ?: return@observer
                var changed = false
                val newTimeline = timeline.map {
                    if (it.timelineItem.uid == timelineItem.timelineItem.uid) {
                        changed = true
                        timelineItem
                    } else {
                        it
                    }
                }
                if (changed) {
                    offer(resource.copy(value = resource.value.copyWithValue(newTimeline)))
                }
            }

            val statusObserver = statusObserver@{ status: Status ->
                val timeline = resource.value.value ?: return@statusObserver
                var changed = false
                val newTimeline = timeline.map {
                    if (it.timelineItem.content?.status?.id == status.id) {
                        changed = true
                        it.copy(
                            timelineItem = it.timelineItem.copy(
                                content = it.timelineItem.content.copy(status = status)
                            )
                        )
                    } else {
                        it
                    }
                }
                if (changed) {
                    offer(resource.copy(value = resource.value.copyWithValue(newTimeline)))
                }
            }

            if (userId == null) {
                // HACK: Don't show the loading state for better UX.
                //offer(ResourceWithMore<List<TimelineItemWithState>>(Loading(null), Deleted(null)))
                getCachedHomeTimeline()?.let {
                    offer(
                        ResourceWithMore(
                            Success(it, refresh),
                            if (it.isNotEmpty()) Success(null, loadMore) else Deleted(null)
                        )
                    )
                }
            }
            refresh()
            observers.add(observer)
            StatusRepository.addObserver(statusObserver)
            awaitClose {
                observers.remove(observer)
                StatusRepository.removeObserver(statusObserver)
            }
        }

    private suspend fun fetchTimeline(
        userId: String?,
        maxId: String? = null
    ): List<TimelineItemWithState> {
        val timeline = if (userId == null) {
            ApiService.getHomeTimeline(maxId)
        } else {
            ApiService.getUserTimeline(userId, maxId)
        }
        return timeline.items.filter { it.type.isNotEmpty() }.withState().also {
            it.forEach { putTimelineItem(it) }
        }
    }

    private suspend fun getCachedHomeTimeline(): List<TimelineItemWithState>? =
        JsonDiskCache.get<List<TimelineItem>>(getHomeTimelineCacheKey(), HOME_TIMELINE_CACHE_TYPE)
            ?.withState()

    suspend fun putCachedHomeTimeline(timeline: List<TimelineItemWithState>) {
        JsonDiskCache.put(getHomeTimelineCacheKey(), timeline.dropState(), HOME_TIMELINE_CACHE_TYPE)
    }

    private fun getHomeTimelineCacheKey(): String =
        "${accountManager.activeAccount!!.name}:home_timeline"

    private fun List<TimelineItem>.withState() =
        map { getTimelineItem(it.uid)?.copy(timelineItem = it) ?: TimelineItemWithState(it) }

    private fun List<TimelineItemWithState>.dropState() =
        map { it.timelineItem }

    suspend fun likeTimelineItem(timelineItemWithState: TimelineItemWithState, liked: Boolean) {
        val timelineItem = timelineItemWithState.timelineItem
        val reactionType = if (liked) React.ReactionType.VOTE else React.ReactionType.CANCEL_VOTE
        updateTimelineItemIsLiking(timelineItem.uid, true)
        val react = try {
            ApiService.react(Uri.parse(timelineItem.uri).path!!, reactionType)
        } catch (e: Exception) {
            Timber.e(e)
            throw e
        } finally {
            updateTimelineItemIsLiking(timelineItem.uid, false)
        }
        updateTimelineItemForReaction(timelineItem.uid, react.reactionType!!)
    }

    private fun updateTimelineItemIsLiking(uid: String, isLiking: Boolean) {
        val timelineItem = getTimelineItem(uid) ?: return
        if (timelineItem.isLiking == isLiking) {
            return
        }
        putTimelineItem(timelineItem.copy(isLiking = isLiking))
    }

    private fun updateTimelineItemForReaction(uid: String, reactionType: React.ReactionType) {
        val timelineItemWithState = getTimelineItem(uid) ?: return
        val timelineItem = timelineItemWithState.timelineItem
        if (timelineItem.reactionType == reactionType) {
            return
        }
        val status = timelineItem.content?.status
        putTimelineItem(
            timelineItemWithState.copy(
                timelineItem = timelineItem.copy(
                    reactionType = reactionType,
                    reactionsCount = timelineItem.reactionsCount + when(reactionType) {
                        React.ReactionType.VOTE -> 1
                        React.ReactionType.CANCEL_VOTE -> -1
                    },
                    content = timelineItem.content?.copy(
                        status = status?.copy(
                            liked = when (reactionType) {
                                React.ReactionType.VOTE -> true
                                React.ReactionType.CANCEL_VOTE -> false
                            },
                            likeCount = status.likeCount + when(reactionType) {
                                React.ReactionType.VOTE -> 1
                                React.ReactionType.CANCEL_VOTE -> -1
                            }
                        )
                    )
                )
            )
        )
    }

    private fun getTimelineItem(uid: String): TimelineItemWithState? = timelineItems[uid]?.get()

    private fun putTimelineItem(timelineItemWithState: TimelineItemWithState) {
        val timelineItem = timelineItemWithState.timelineItem
        val changed = getTimelineItem(timelineItem.uid) != timelineItemWithState
        timelineItems[timelineItem.uid] = WeakReference(timelineItemWithState)
        if (changed) {
            observers.forEach { it(timelineItemWithState) }
        }

        timelineItem.content?.status?.let {
            StatusRepository.putStatus(it)
        }
    }
}
