/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network

object Http {
    object Headers {
        const val AUTHORIZATION = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
        fun String.getAccessToken() =
            if (startsWith(BEARER_PREFIX)) drop(BEARER_PREFIX.length) else null
        fun String.toBearerAuthentication() = "$BEARER_PREFIX$this"

        const val CONTENT_DISPOSITION = "Content-Disposition"

        const val CONTENT_LENGTH = "Content-Length"

        const val USER_AGENT = "User-Agent"
    }
}
