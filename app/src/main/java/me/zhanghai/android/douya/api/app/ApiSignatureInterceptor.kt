/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.app

import android.net.Uri
import android.util.Base64
import me.zhanghai.android.douya.api.info.ApiContract
import me.zhanghai.android.douya.network.Http
import me.zhanghai.android.douya.network.addParameters
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

private const val ALGORITHM_HMAC_SHA1 = "HmacSHA1"

/**
 * See ApiSignatureHelper and ApiSignatureInterceptor in Frodo.
 */
class ApiSignatureInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (request.url.host !in ApiContract.Api.SIGNATURE_HOSTS) {
            return chain.proceed(request)
        }
        val timestamp = createTimestamp()
        val signature = createSignature(request, timestamp)
        request = request.newBuilder()
            .addParameters(
                request,
                mapOf(ApiContract.Api.SIG to signature, ApiContract.Api.TS to timestamp))
            .build()
        return chain.proceed(request)
    }

    private fun createTimestamp(): String {
        return (System.currentTimeMillis() / 1000).toString()
    }

    private fun createSignature(request: Request, timestamp: String): String {
        return hmacSha1(
            ApiContract.Credential.SECRET,
            StringBuilder()
                .apply {
                    append(request.method)
                    var path = request.url.encodedPath
                    path = Uri.decode(path)
                    if (path.endsWith("/")) {
                        path = path.dropLast(1)
                    }
                    path = Uri.encode(path)
                    append("&").append(path)
                    val authToken = request.header(Http.Headers.AUTHORIZATION)?.drop(7)
                    if (!authToken.isNullOrEmpty()) {
                        append("&").append(authToken)
                    }
                    append("&").append(timestamp)
                }
                .toString()
        )
    }

    // Would have used getBytes(StandardCharsetsCompat.UTF_8) if not conforming to Frodo.
    private fun hmacSha1(key: String, input: String): String {
        val mac = Mac.getInstance(ALGORITHM_HMAC_SHA1)
        val keySpec = SecretKeySpec(key.toByteArray(), ALGORITHM_HMAC_SHA1)
        mac.init(keySpec)
        val result = mac.doFinal(input.toByteArray())
        return Base64.encodeToString(result, Base64.NO_WRAP)
    }
}
