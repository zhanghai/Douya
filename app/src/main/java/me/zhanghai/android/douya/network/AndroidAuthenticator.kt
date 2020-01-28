/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network

import android.accounts.Account
import me.zhanghai.android.douya.account.app.blockingGetAuthToken
import me.zhanghai.android.douya.account.app.invalidateAuthToken
import me.zhanghai.android.douya.network.Http.Headers.getAccessToken
import me.zhanghai.android.douya.network.Http.Headers.toBearerAuthentication
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

abstract class AndroidAuthenticator(
    private val account: Account
) : AuthenticationInterceptor.Authenticator {
    @Throws(IOException::class)
    override fun authenticate(request: Request): Request {
        val authToken = try {
            account.blockingGetAuthToken(true)
        } catch (e: Exception) {
            throw AuthenticationException(e)
        } ?: throw AuthenticationException("AccountManager.blockingGetAuthToken() returned null")
        return request.newBuilder()
            .header(Http.Headers.AUTHORIZATION, authToken.toBearerAuthentication())
            .build()
    }

    @Throws(IOException::class)
    override fun retryAuthentication(response: Response): Request? {
        if (!shouldRetryAuthentication(response)) {
            return null
        }
        val authToken = response.request.header(Http.Headers.AUTHORIZATION)?.getAccessToken()
        authToken?.let { account.invalidateAuthToken(it) }
        return authenticate(response.request)
    }

    abstract fun shouldRetryAuthentication(response: Response): Boolean
}
