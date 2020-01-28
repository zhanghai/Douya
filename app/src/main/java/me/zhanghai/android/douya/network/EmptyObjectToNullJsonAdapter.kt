/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type

class EmptyObjectToNullJsonAdapter<T>(private val adapter: JsonAdapter<T>) : JsonAdapter<T>() {

    override fun fromJson(reader: JsonReader) =
        if (reader.peekJson().runCatching {
                beginObject()
                endObject()
            }.isSuccess) {
            reader.run {
                beginObject()
                endObject()
            }
            null
        } else {
            adapter.fromJson(reader)
        }

    override fun toJson(writer: JsonWriter, value: T?) = adapter.toJson(writer, value)

    object Factory : JsonAdapter.Factory {

        override fun create(
            type: Type,
            annotations: MutableSet<out Annotation>,
            moshi: Moshi
        ): JsonAdapter<*>? {
            val delegateAnnotations = Types.nextAnnotations(annotations,
                EmptyObjectToNull::class.java) ?: return null
            return EmptyObjectToNullJsonAdapter(moshi.adapter<Any>(type, delegateAnnotations))
        }
    }
}
