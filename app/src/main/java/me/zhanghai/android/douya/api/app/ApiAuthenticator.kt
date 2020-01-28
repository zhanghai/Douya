/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.app

import android.accounts.Account
import me.zhanghai.android.douya.api.info.ApiContract
import me.zhanghai.android.douya.network.AndroidAuthenticator
import okhttp3.Response
import timber.log.Timber
import java.io.IOException

class ApiAuthenticator(account: Account) : AndroidAuthenticator(account) {
    override fun shouldRetryAuthentication(response: Response): Boolean {
        // Don't consume the response body so that it can later be read by others.
        val body = try {
            response.peekBody(Long.MAX_VALUE)
        } catch (e: IOException) {
            Timber.e(e)
            return false
        }
        return when (body.toErrorResponse()?.code) {
            ApiContract.Error.Codes.INVALID_ACCESS_TOKEN,
            ApiContract.Error.Codes.ACCESS_TOKEN_HAS_EXPIRED,
            ApiContract.Error.Codes.ACCESS_TOKEN_HAS_EXPIRED_SINCE_PASSWORD_CHANGED -> true
            else -> false
        }
    }
}
