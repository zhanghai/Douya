/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.app

import me.zhanghai.android.douya.api.info.ApiContract
import me.zhanghai.android.douya.network.Http
import me.zhanghai.android.douya.network.addParameters
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ApiKeyInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request.newBuilder()
            .header(Http.Headers.USER_AGENT, ApiContract.Api.USER_AGENT)
            .addParameters(
                request,
                mapOf(
                    ApiContract.Api.OS_ROM to ApiContract.Api.OsRoms.ANDROID,
                    ApiContract.Api.API_KEY to ApiContract.Credential.KEY,
                    ApiContract.Api.CHANNEL to ApiContract.Api.Channels.DOUBAN
                    // TODO: UUID
                )
            )
            .build()
        return chain.proceed(request)
    }
}
