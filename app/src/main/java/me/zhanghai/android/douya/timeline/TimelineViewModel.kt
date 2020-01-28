/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.zhanghai.android.douya.api.app.ApiService
import me.zhanghai.android.douya.api.info.TimelineItem
import me.zhanghai.android.douya.arch.DistinctMutableLiveData
import me.zhanghai.android.douya.arch.Error
import me.zhanghai.android.douya.arch.EventLiveData
import me.zhanghai.android.douya.arch.Loading
import me.zhanghai.android.douya.arch.MutableLiveData
import me.zhanghai.android.douya.arch.ResourceWithMore

class TimelineViewModel(
    private val diffCallbackFactory: (List<TimelineItem>) -> DiffUtil.Callback
) : ViewModel() {
    private lateinit var resource: ResourceWithMore<List<TimelineItem>>

    private val _timeline = MutableLiveData<Pair<List<TimelineItem>, DiffUtil.DiffResult>>()
    val timeline: LiveData<Pair<List<TimelineItem>, DiffUtil.DiffResult>> = _timeline

    val refreshing = DistinctMutableLiveData(false)

    private val _loading = DistinctMutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _empty = DistinctMutableLiveData(false)
    val empty: LiveData<Boolean> = _empty

    private val _error = DistinctMutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _moreLoading = DistinctMutableLiveData(false)
    val moreLoading: LiveData<Boolean> = _moreLoading

    private val _moreErrorEvent = EventLiveData<String>()
    val moreErrorEvent: LiveData<String> = _moreErrorEvent

    init {
        viewModelScope.launch {
            TimelineRepository.observeHomeTimeline().collect { resource ->
                val timeline = resource.value.value ?: emptyList()
                val diffResult = withContext(Dispatchers.Default) {
                    DiffUtil.calculateDiff(diffCallbackFactory(timeline), false)
                }
                _timeline.value = Pair(timeline, diffResult)
                val loading = resource.value is Loading
                val empty = timeline.isEmpty()
                val error = (resource.value as? Error)?.exception?.let {
                    ApiService.errorMessage(it)
                }
                refreshing.value = loading && !empty
                _loading.value = loading && empty
                _empty.value = empty && !loading && error == null
                _error.value = error
                _moreLoading.value = resource.more is Loading
                val moreError = (resource.more as? Error)?.exception?.let {
                    ApiService.errorMessage(it)
                }
                moreError?.let { _moreErrorEvent.value = it }
                this@TimelineViewModel.resource = resource
            }
        }
    }

    fun refresh() = viewModelScope.launch {
        resource.value.refresh?.invoke()
    }

    fun loadMore() = viewModelScope.launch {
        resource.more.refresh?.invoke()
    }
}
