/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.app

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import me.zhanghai.android.douya.R
import me.zhanghai.android.douya.api.info.ApiContract
import me.zhanghai.android.douya.api.info.ErrorResponse
import me.zhanghai.android.douya.app.appContext
import me.zhanghai.android.douya.network.AuthenticationException
import okhttp3.ResponseBody
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun ResponseBody.toErrorResponse() = try {
    ApiService.errorResponseConverter.convert(this)
} catch (e: Exception) {
    Timber.e(e)
    null
}

val HttpException.errorResponse
    get() = response()?.errorBody()?.toErrorResponse()

val ErrorResponse.message
    get() = localizedMessage
        ?: ApiContract.Error.MESSAGES[code]?.let { appContext.getString(it) }
        ?: internalMessage

// @see com.douban.frodo.network.ErrorMessageHelper
val Exception.apiMessage: String
    get() = when (this) {
        is HttpException -> errorResponse?.message
            ?: appContext.getString(R.string.api_error_parse)
        is JsonDataException, is JsonEncodingException, is KotlinNullPointerException ->
            appContext.getString(R.string.api_error_parse)
        is AuthenticationException ->
            appContext.getString(R.string.api_error_authentication)
        is SocketTimeoutException -> appContext.getString(R.string.api_error_timeout)
        is UnknownHostException -> appContext.getString(R.string.api_error_no_connection)
        is IOException -> appContext.getString(R.string.api_error_network)
        else -> toString()
    }
