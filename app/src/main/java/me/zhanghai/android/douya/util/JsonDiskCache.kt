/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

import androidx.collection.LruCache
import com.jakewharton.disklrucache.DiskLruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.zhanghai.android.douya.BuildConfig
import me.zhanghai.android.douya.api.app.moshi
import me.zhanghai.android.douya.app.application
import okio.buffer
import okio.source
import java.io.File
import java.lang.reflect.Type
import java.security.MessageDigest

object JsonDiskCache {
    private const val MAX_SIZE_BYTES: Long = 2 * 1024 * 1024

    private val cache = DiskLruCache.open(
        File(application.cacheDir, BuildConfig.APPLICATION_ID), 0, 1, MAX_SIZE_BYTES
    )

    suspend fun <T> get(key: String, type: Type): T? =
        withContext(Dispatchers.IO) {
            synchronized(cache) {
                val source = cache.get(CacheKeys.get(key))?.getInputStream(0)?.source()
                    ?.buffer() ?: return@withContext null
                source.use {
                    moshi.adapter<T>(type).fromJson(it)
                }
            }
        }

    suspend inline fun <reified T> get(key: String) = get<T>(key, T::class.java)

    suspend fun <T> put(key: String, value: T?, type: Type) {
        if (value == null) {
            remove(key)
            return
        }
        withContext(Dispatchers.IO) {
            val json = moshi.adapter<T>(type).toJson(value)
            synchronized(cache) {
                cache.edit(CacheKeys.get(key)) { set(0, json) }
            }
        }
    }

    suspend inline fun <reified T> put(key: String, value: T?) = put(key, value, T::class.java)

    fun DiskLruCache.edit(key: String, block: DiskLruCache.Editor.() -> Unit) {
        edit(key).apply {
            try {
                block()
                commit()
            } finally {
                abortUnlessCommitted()
            }
        }
    }

    suspend fun remove(key: String) {
        withContext(Dispatchers.IO) {
            synchronized(cache) {
                cache.remove(CacheKeys.get(key))
            }
        }
    }

    private object CacheKeys {
        private val cache: LruCache<String, String> = LruCache(100)

        fun get(key: String): String {
            var cacheKey = synchronized(cache) { cache.get(key) }
            if (cacheKey == null) {
                val messageDigest = MessageDigest.getInstance("SHA-256")
                messageDigest.update(key.toByteArray())
                cacheKey = messageDigest.digest().toHexString()
                synchronized(cache) { cache.put(key, cacheKey) }
            }
            return cacheKey
        }
    }
}
