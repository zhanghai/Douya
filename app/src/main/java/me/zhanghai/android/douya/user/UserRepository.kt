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
    private val users = mutableMapOf<String, WeakReference<User>>()
    private val observers = mutableSetOf<(User) -> Unit>()

    fun observeUser(id: String): Flow<Resource<User>> =
        callbackFlow {
            var resource: Resource<User> = Deleted(getUser(id))
            val offer = { newResource: Resource<User> ->
                resource = newResource
                channel.offer(resource)
            }
            var refresh = suspend {}

            refresh = refresh@{
                offer(Loading(resource.value))
                val user = try {
                    fetchUser(id)
                } catch (e: Exception) {
                    Timber.e(e)
                    offer(Error(resource.value, e, refresh))
                    return@refresh
                }
                offer(Success(user, refresh))
            }

            val observer: (User) -> Unit = { offer(resource.copyWithValue(it)) }

            refresh()
            observers.add(observer)
            awaitClose { observers.remove(observer) }
        }

    private suspend fun fetchUser(id: String): User =
        ApiService.getUser(id).also { putUser(it) }

    private fun getUser(id: String): User? =
        users[id]?.get()

    private fun putUser(user: User) {
        val changed = getUser(user.id) != user
        users[user.id] = WeakReference(user)
        if (changed) {
            observers.forEach { it(user) }
        }
    }
}
