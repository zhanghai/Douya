/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.io.IOException
import java.lang.annotation.ElementType
import java.lang.reflect.Type

class UnknownEnumToNullJsonAdapter<T : Enum<T>>(
    private val clazz: Class<T>
) : JsonAdapter<T>() {
    private val values = clazz.enumConstants!!
    private val names = values.map {
        clazz.getField(it.name).getAnnotation(Json::class.java)?.name ?: it.name
    }

    @Throws(IOException::class)
    override fun fromJson(reader: JsonReader): T? =
        if (reader.peek() == JsonReader.Token.NULL) {
            reader.nextNull()
        } else {
            val name = reader.nextString()
            names.indexOfFirst { it == name }.takeIf { it != -1 }?.let { values[it] }
        }

    @Throws(IOException::class)
    override fun toJson(writer: JsonWriter, value: T?) {
        writer.value(value?.let { names[it.ordinal] })
    }

    object Factory : JsonAdapter.Factory {
        override fun create(
            type: Type,
            annotations: MutableSet<out Annotation>,
            moshi: Moshi
        ): JsonAdapter<*>? {
            val rawType = Types.getRawType(type)
            return if (rawType.isEnum) {
                // HACK: I didn't find a way to cast this properly in Kotlin, but thanks to erasure
                // we can hack like this.
                @Suppress("UNCHECKED_CAST")
                UnknownEnumToNullJsonAdapter(rawType as Class<ElementType>)
            } else {
                null
            }
        }
    }
}
