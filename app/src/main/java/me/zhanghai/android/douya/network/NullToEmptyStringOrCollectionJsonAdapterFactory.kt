/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type

object NullToEmptyStringOrCollectionJsonAdapterFactory : JsonAdapter.Factory {

    override fun create(
        type: Type,
        annotations: MutableSet<out Annotation>,
        moshi: Moshi
    ): JsonAdapter<*>? {
        val rawType = Types.getRawType(type)
        val delegateAdapter by lazy { moshi.nextAdapter<Any>(this, type, annotations) }
        return when (rawType) {
            String::class.java -> NullFallbackJsonAdapter(delegateAdapter, "")
            List::class.java, Collection::class.java ->
                NullFallbackJsonAdapter(delegateAdapter, emptyList<Any>())
            Set::class.java -> NullFallbackJsonAdapter(delegateAdapter, emptySet<Any>())
            else -> null
        }
    }
}
