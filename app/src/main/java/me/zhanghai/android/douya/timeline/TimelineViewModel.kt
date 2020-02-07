/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.zhanghai.android.douya.api.app.apiMessage
import me.zhanghai.android.douya.api.info.TimelineItem
import me.zhanghai.android.douya.arch.DistinctMutableLiveData
import me.zhanghai.android.douya.arch.Error
import me.zhanghai.android.douya.arch.EventLiveData
import me.zhanghai.android.douya.arch.Loading
import me.zhanghai.android.douya.arch.ResourceWithMore
import me.zhanghai.android.douya.arch.mapDistinct
import me.zhanghai.android.douya.util.takeIfNotEmpty

class TimelineViewModel(
    userId: String?,
    private val diffCallbackFactory: (List<TimelineItem>) -> DiffUtil.Callback
) : ViewModel() {
    private lateinit var resource: ResourceWithMore<List<TimelineItem>>

    data class State(
        val timeline: Pair<List<TimelineItem>, DiffUtil.DiffResult?>,
        val loading: Boolean,
        val empty: Boolean,
        val error: String,
        val moreLoading: Boolean
    )

    private val state = MutableLiveData(
        State(
            timeline = Pair(emptyList(), null),
            loading = false,
            empty = false,
            error = "",
            moreLoading = false
        )
    )
    val timeline = state.mapDistinct { it.timeline }
    val loading = state.mapDistinct { it.loading }
    val empty = state.mapDistinct { it.empty }
    val error = state.mapDistinct { it.error }
    val moreLoading = state.mapDistinct { it.moreLoading }

    val refreshing = DistinctMutableLiveData(false)

    private val _errorEvent = EventLiveData<String>()
    val errorEvent: LiveData<String> = _errorEvent

    init {
        viewModelScope.launch {
            TimelineRepository.observeTimeline(userId).collect { resource ->
                val timeline = resource.value.value ?: emptyList()
                val diffResult = if (!refreshing.value) {
                    withContext(Dispatchers.Default) {
                        DiffUtil.calculateDiff(diffCallbackFactory(timeline), false)
                    }
                } else {
                    null
                }
                val loading = resource.value is Loading
                val empty = timeline.isEmpty()
                val error = (resource.value as? Error)?.exception?.apiMessage ?: ""
                state.value = State(
                    timeline = Pair(timeline, diffResult),
                    loading = loading && empty,
                    empty = empty && !loading && error.isEmpty(),
                    error = error.takeIf { empty } ?: "",
                    moreLoading = resource.more is Loading
                )
                refreshing.value = loading && !empty
                error.takeIfNotEmpty()?.let { _errorEvent.value = it }
                val moreError = (resource.more as? Error)?.exception?.apiMessage ?: ""
                moreError.takeIfNotEmpty()?.let { _errorEvent.value = it }
                this@TimelineViewModel.resource = resource
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            resource.value.refresh?.invoke()
        }
    }

    fun loadMore() {
        viewModelScope.launch {
            resource.more.refresh?.invoke()
        }
    }
}
