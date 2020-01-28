/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.HttpRetryException

// We cannot use [okhttp3.Authenticator] because it only retries on HTTP 401.
// @see okhttp3.internal.http.RetryAndFollowUpInterceptor
abstract class AuthenticationInterceptor(
    private val maxRetries: Int
) : Interceptor {
    protected abstract var authenticator: Authenticator

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var retryCount = 0
        val authenticator = authenticator
        var request: Request = authenticator.authenticate(chain.request())
        while (true) {
            val response = chain.proceed(request)
            if (response.isSuccessful || retryCount >= maxRetries) {
                return response
            }
            request = authenticator.retryAuthentication(response) ?: return response
            response.body?.close()
            if (request.body?.isOneShot() == true) {
                throw HttpRetryException("Cannot retry with one-shot request body", response.code)
            }
            ++retryCount
        }
    }

    interface Authenticator {
        @Throws(IOException::class)
        fun authenticate(request: Request): Request

        @Throws(IOException::class)
        fun retryAuthentication(response: Response): Request?
    }
}
