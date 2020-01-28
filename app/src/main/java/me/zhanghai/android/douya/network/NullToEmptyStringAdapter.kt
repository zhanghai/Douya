/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

object NullToEmptyStringAdapter : JsonAdapter<String>() {

    override fun fromJson(reader: JsonReader): String =
        if (reader.peek() == JsonReader.Token.NULL) {
            reader.nextNull<Nothing>()
            ""
        } else {
            reader.nextString()
        }

    override fun toJson(writer: JsonWriter, value: String?) {
        writer.value(value.orEmpty())
    }
}
