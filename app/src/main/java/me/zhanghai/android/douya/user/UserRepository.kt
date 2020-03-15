/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.user

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import me.zhanghai.android.douya.api.app.ApiService
import me.zhanghai.android.douya.api.info.User
import me.zhanghai.android.douya.arch.Deleted
import me.zhanghai.android.douya.arch.Error
import me.zhanghai.android.douya.arch.Loading
import me.zhanghai.android.douya.arch.Resource
import me.zhanghai.android.douya.arch.Success
import timber.log.Timber
import java.lang.ref.WeakReference

object UserRepository {
    private val cachedUsers = mutableMapOf<String, WeakReference<User>>()
    private val observers = mutableSetOf<(User) -> Unit>()

    fun observeUser(userId: String): Flow<Resource<User>> =
        callbackFlow {
            var resource: Resource<User> = Deleted(getCachedUser(userId))
            val offer = { newResource: Resource<User> ->
                resource = newResource
                channel.offer(resource)
            }
            var refresh = suspend {}

            refresh = refresh@{
                offer(Loading(resource.value))
                val user = try {
                    getUser(userId)
                } catch (e: Exception) {
                    Timber.e(e)
                    offer(Error(resource.value, e, refresh))
                    return@refresh
                }
                offer(Success(user, refresh))
            }

            val observer: (User) -> Unit = { offer(resource.copyWithValue(it)) }

            refresh()
            addObserver(observer)
            awaitClose { removeObserver(observer) }
        }

    private suspend fun getUser(userId: String): User =
        ApiService.getUser(userId).also { putCachedUser(it) }

    fun getCachedUser(userId: String): User? =
        cachedUsers[userId]?.get()

    fun putCachedUser(user: User) {
        val changed = cachedUsers[user.id]?.get() != user
        cachedUsers[user.id] = WeakReference(user)
        if (changed) {
            observers.forEach { it(user) }
        }
    }

    fun addObserver(observer: (User) -> Unit) {
        observers.add(observer)
    }

    fun removeObserver(observer: (User) -> Unit) {
        observers.remove(observer)
    }
}
