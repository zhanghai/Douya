/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.io.IOException

class NullFallbackJsonAdapter<T>(
    private val delegateAdapter: JsonAdapter<T>,
    private val fallbackValue: T
) : JsonAdapter<T>() {
    @Throws(IOException::class)
    override fun fromJson(reader: JsonReader): T? =
        if (reader.peek() === JsonReader.Token.NULL) {
            reader.nextNull<Nothing>()
            fallbackValue
        } else {
            delegateAdapter.fromJson(reader)
        }

    @Throws(IOException::class)
    override fun toJson(writer: JsonWriter, value: T?) {
        delegateAdapter.toJson(writer, value ?: fallbackValue)
    }
}
