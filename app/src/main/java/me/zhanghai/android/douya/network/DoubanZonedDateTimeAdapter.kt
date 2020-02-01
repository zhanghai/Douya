/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import me.zhanghai.android.douya.util.ZonedDateTimes
import me.zhanghai.android.douya.util.formatDouban
import me.zhanghai.android.douya.util.parseDouban
import org.threeten.bp.ZonedDateTime
import java.io.IOException

object DoubanZonedDateTimeAdapter : JsonAdapter<ZonedDateTime>() {

    @Throws(IOException::class)
    override fun fromJson(reader: JsonReader): ZonedDateTime? =
        if (reader.peek() == JsonReader.Token.NULL) {
            reader.nextNull()
        } else {
            val text = reader.nextString()
            if (text.isNotEmpty()) ZonedDateTimes.parseDouban(text) else null
        }

    @Throws(IOException::class)
    override fun toJson(writer: JsonWriter, value: ZonedDateTime?) {
        if (value == null) {
            writer.nullValue()
        } else {
            writer.value(value.formatDouban())
        }
    }
}
