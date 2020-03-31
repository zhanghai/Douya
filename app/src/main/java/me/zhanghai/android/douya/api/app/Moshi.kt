/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.app

import com.squareup.moshi.Moshi
import me.zhanghai.android.douya.network.DoubanZonedDateTimeAdapter
import me.zhanghai.android.douya.network.EmptyObjectToNullJsonAdapter
import me.zhanghai.android.douya.network.NullToEmptyStringOrCollectionJsonAdapterFactory
import me.zhanghai.android.douya.network.UnknownEnumToNullJsonAdapter
import org.threeten.bp.ZonedDateTime

val moshi: Moshi = Moshi.Builder()
    .add(NullToEmptyStringOrCollectionJsonAdapterFactory)
    .add(UnknownEnumToNullJsonAdapter.Factory)
    .add(EmptyObjectToNullJsonAdapter.Factory)
    .add(ZonedDateTime::class.java, DoubanZonedDateTimeAdapter)
    .build()
