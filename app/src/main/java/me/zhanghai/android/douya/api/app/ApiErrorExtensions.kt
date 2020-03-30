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
import me.zhanghai.android.douya.app.application
import me.zhanghai.android.douya.network.AuthenticationException
import me.zhanghai.android.douya.util.takeIfNotEmpty
import okhttp3.ResponseBody
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun ResponseBody.toErrorResponse(): ErrorResponse? =
    try {
        ApiService.errorResponseConverter.convert(this)
    } catch (e: Exception) {
        Timber.e(e)
        null
    }

val HttpException.errorResponse: ErrorResponse?
    get() = response()?.errorBody()?.toErrorResponse()

val ErrorResponse.message: String
    get() = localizedMessage?.takeIfNotEmpty()
        ?: ApiContract.Error.MESSAGES[code]?.let { application.getString(it) }
        ?: internalMessage

// @see com.douban.frodo.network.ErrorMessageHelper
val Exception.apiMessage: String
    get() = when (this) {
        is HttpException -> errorResponse?.message
            ?: application.getString(R.string.api_error_parse)
        is JsonDataException, is JsonEncodingException, is KotlinNullPointerException ->
            application.getString(R.string.api_error_parse)
        is AuthenticationException ->
            application.getString(R.string.api_error_authentication)
        is SocketTimeoutException -> application.getString(R.string.api_error_timeout)
        is UnknownHostException -> application.getString(R.string.api_error_no_connection)
        is IOException -> application.getString(R.string.api_error_network)
        else -> toString()
    }
