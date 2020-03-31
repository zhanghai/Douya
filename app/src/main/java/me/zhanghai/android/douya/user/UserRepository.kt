/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.user

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import me.zhanghai.android.douya.account.app.activeAccount
import me.zhanghai.android.douya.account.app.userId
import me.zhanghai.android.douya.api.app.ApiService
import me.zhanghai.android.douya.api.info.User
import me.zhanghai.android.douya.app.accountManager
import me.zhanghai.android.douya.arch.Deleted
import me.zhanghai.android.douya.arch.Error
import me.zhanghai.android.douya.arch.Loading
import me.zhanghai.android.douya.arch.Resource
import me.zhanghai.android.douya.arch.Success
import me.zhanghai.android.douya.util.JsonDiskCache
import timber.log.Timber
import java.lang.ref.WeakReference

object UserRepository {
    private val users = mutableMapOf<String, WeakReference<User>>()
    private val observers = mutableSetOf<(User) -> Unit>()

    fun observeUser(id: String): Flow<Resource<User>> =
        callbackFlow {
            var resource: Resource<User> = Deleted(null)
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

            val user = getUser(id)
            if (user != null) {
                offer(Success(user, refresh))
            } else if (shouldCacheUser(id)) {
                offer(Loading(null))
                getCachedUser(id)?.let { offer(Success(it, refresh)) }
            }
            refresh()
            observers.add(observer)
            awaitClose { observers.remove(observer) }
        }

    private suspend fun fetchUser(id: String): User =
        ApiService.getUser(id).also { putUser(it) }

    private fun shouldCacheUser(id: String): Boolean =
        accountManager.accounts.any { it.userId == id }

    private suspend fun getCachedUser(id: String): User? =
        JsonDiskCache.get(getCacheKey(id))

    private suspend fun putCachedUser(user: User) {
        JsonDiskCache.put(getCacheKey(user.id), user)
    }

    private fun getCacheKey(id: String): String =
        "${accountManager.activeAccount!!.name}:user:${id}"

    private fun getUser(id: String): User? =
        users[id]?.get()

    private fun putUser(user: User) {
        val changed = getUser(user.id) != user
        users[user.id] = WeakReference(user)
        if (changed) {
            if (shouldCacheUser(user.id)) {
                GlobalScope.launch(Dispatchers.Main.immediate) {
                    putCachedUser(user)
                }
            }
            observers.forEach { it(user) }
        }
    }
}
