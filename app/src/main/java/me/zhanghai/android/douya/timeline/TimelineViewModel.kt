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
import me.zhanghai.android.douya.arch.Deleted
import me.zhanghai.android.douya.arch.DistinctMutableLiveData
import me.zhanghai.android.douya.arch.Error
import me.zhanghai.android.douya.arch.Loading
import me.zhanghai.android.douya.arch.MutableLiveData
import me.zhanghai.android.douya.arch.Refreshable
import me.zhanghai.android.douya.arch.ResourceWithMore
import me.zhanghai.android.douya.arch.Success

class TimelineViewModel(
    private val diffCallbackFactory: (List<TimelineItem>) -> DiffUtil.Callback
) : ViewModel() {

    private lateinit var resource: ResourceWithMore<List<TimelineItem>>

    private val _timeline = MutableLiveData<Pair<List<TimelineItem>, DiffUtil.DiffResult>>()
    val timeline: LiveData<Pair<List<TimelineItem>, DiffUtil.DiffResult>> = _timeline

    private val _loading = DistinctMutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _empty = DistinctMutableLiveData(false)
    val empty: LiveData<Boolean> = _empty

    private val _error = DistinctMutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _moreAvailable = DistinctMutableLiveData(false)
    val moreAvailable: LiveData<Boolean> = _moreAvailable

    private val _moreLoading = DistinctMutableLiveData(false)
    val moreLoading: LiveData<Boolean> = _moreLoading

    private val _moreError = DistinctMutableLiveData<String?>(null)
    val moreError: LiveData<String?> = _moreError

    init {
        viewModelScope.launch {
            TimelineRepository.observeHomeTimeline().collect { resource ->
                val timeline = if (resource.value is Success) resource.value.value else emptyList()
                val diffResult = withContext(Dispatchers.Default) {
                    DiffUtil.calculateDiff(diffCallbackFactory(timeline), false)
                }
                _timeline.value = Pair(timeline, diffResult)
                _loading.value = resource.value == Loading
                _empty.value = resource.value is Success && resource.value.value.isEmpty()
                _error.value = if (resource.value is Error) {
                    ApiService.errorMessage(resource.value.exception)
                } else {
                    null
                }
                _moreAvailable.value = resource.more != Deleted
                _moreLoading.value = resource.more == Loading
                _moreError.value = if (resource.more is Error) {
                    ApiService.errorMessage(resource.more.exception)
                } else {
                    null
                }
                this@TimelineViewModel.resource = resource
            }
        }
    }

    fun refresh() = viewModelScope.launch {
        (resource as Refreshable).refresh()
    }

    fun loadMore() = viewModelScope.launch {
        (resource.more as Refreshable).refresh()
    }
}
