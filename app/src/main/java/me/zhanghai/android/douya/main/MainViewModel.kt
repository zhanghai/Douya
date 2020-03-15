/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.zhanghai.android.douya.api.info.User
import me.zhanghai.android.douya.api.util.uriOrUrl
import me.zhanghai.android.douya.arch.EventLiveData
import me.zhanghai.android.douya.arch.Resource
import me.zhanghai.android.douya.arch.mapDistinct
import me.zhanghai.android.douya.arch.valueCompat
import me.zhanghai.android.douya.user.UserRepository
import me.zhanghai.android.douya.util.takeIfNotEmpty

class MainViewModel(
    userId: String
) : ViewModel() {
    private lateinit var userResource: Resource<User>

    data class State(
        val userAvatarUrl: String,
        val userName: String,
        val userUri: String
    ) {
        companion object {
            val INITIAL = State(
                userAvatarUrl = "",
                userName = "",
                userUri = ""
            )
        }
    }

    private val state = MutableLiveData(State.INITIAL)

    val userAvatarUrl = state.mapDistinct { it.userAvatarUrl }
    val userName = state.mapDistinct { it.userName }

    private val _openUriEvent = EventLiveData<String>()
    val openUriEvent: LiveData<String> = _openUriEvent

    private val _sendStatusEvent = EventLiveData<Unit>()
    val sendStatusEvent: LiveData<Unit> = _sendStatusEvent

    init {
        viewModelScope.launch {
            UserRepository.observeUser(userId).collect { resource ->
                val user = resource.value
                state.value = State(
                    userAvatarUrl = user?.avatar ?: "",
                    userName = user?.name ?: "",
                    userUri = user?.uriOrUrl ?: ""
                )
                userResource = resource
            }
        }
    }

    fun openUser() {
        state.valueCompat.userUri.takeIfNotEmpty()?.let { _openUriEvent.value = it }
    }

    fun sendStatus() {
        _sendStatusEvent.value = Unit
    }
}
