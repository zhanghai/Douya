/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network

import okhttp3.FormBody
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.internal.http.HttpMethod.requiresRequestBody

fun Request.Builder.addParameters(
    request: Request,
    parameters: Map<String, String>
): Request.Builder {
    if (!requiresRequestBody(request.method)) {
        url(request.url.newBuilder()
            .apply {
                for ((name, value) in parameters) {
                    removeAllQueryParameters(name)
                    addQueryParameter(name, value)
                }
            }
            .build())
    } else {
        val requestBody = request.body
        val body = if (requestBody == null || requestBody.contentLength() == 0L) {
            FormBody.Builder()
                .apply {
                    for ((name, value) in parameters) {
                        add(name, value)
                    }
                }
                .build()
        } else if (requestBody is FormBody) {
            FormBody.Builder()
                .apply {
                    for (i in 0 until requestBody.size) {
                        val name = requestBody.name(i)
                        if (name in parameters) {
                            continue
                        }
                        add(name, requestBody.value(i))
                    }
                    for ((name, value) in parameters) {
                        add(name, value)
                    }
                }
                .build()
        } else if (requestBody is MultipartBody) {
            MultipartBody.Builder(requestBody.boundary)
                .apply {
                    setType(requestBody.type)
                    val parameterParts = parameters.map {
                        MultipartBody.Part.createFormData(it.key, it.value)
                    }
                    val parameterContentDispositions = parameterParts.mapNotNullTo(mutableSetOf()) {
                        it.headers?.get(Http.Headers.CONTENT_DISPOSITION)
                    }
                    for (i in 0 until requestBody.size) {
                        val part = requestBody.part(i)
                        val contentDisposition = part.headers?.get(Http.Headers.CONTENT_DISPOSITION)
                        if (contentDisposition != null
                            && contentDisposition in parameterContentDispositions) {
                            continue
                        }
                        addPart(part)
                    }
                    for (parameterPart in parameterParts) {
                        addPart(parameterPart)
                    }
                }
                .build()
        } else {
            throw UnsupportedOperationException("Unsupported request body: $requestBody")
        }
        method(request.method, body)
        removeHeader(Http.Headers.CONTENT_LENGTH)
        header(Http.Headers.CONTENT_LENGTH, body.contentLength().toString())
    }
    return this
}
