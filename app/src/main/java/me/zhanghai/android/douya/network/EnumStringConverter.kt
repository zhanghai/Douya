/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network

import com.squareup.moshi.Json
import com.squareup.moshi.Types
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.annotation.ElementType
import java.lang.reflect.Type

class EnumStringConverter<T : Enum<T>>(
    private val clazz: Class<T>
) : Converter<T, String> {
    private val names = clazz.enumConstants!!.map {
        clazz.getField(it.name).getAnnotation(Json::class.java)?.name ?: it.name
    }

    override fun convert(value: T?): String? =
        value?.let { names[it.ordinal] }

    object Factory : Converter.Factory() {
        override fun stringConverter(
            type: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit
        ): Converter<*, String>? {
            val rawType = Types.getRawType(type)
            return if (rawType.isEnum) {
                // HACK: I didn't find a way to cast this properly in Kotlin, but thanks to erasure
                // we can hack like this.
                @Suppress("UNCHECKED_CAST")
                EnumStringConverter(rawType as Class<ElementType>)
            } else {
                null
            }
        }
    }
}
