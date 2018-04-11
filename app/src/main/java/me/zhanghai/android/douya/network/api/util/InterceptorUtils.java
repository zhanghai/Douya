/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.zhanghai.android.douya.network.Http;
import me.zhanghai.android.douya.util.LogUtils;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.internal.http.HttpMethod;

public class InterceptorUtils {

    private InterceptorUtils() {}

    public static void addParameters(Request.Builder builder, Request request,
                                     Map<String, String> parameters) throws IOException {
        String method = request.method();
        if (!HttpMethod.requiresRequestBody(method)) {
            HttpUrl.Builder urlBuilder = request.url().newBuilder();
            for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                String parameterName = parameter.getKey();
                urlBuilder.removeAllQueryParameters(parameterName);
                urlBuilder.addQueryParameter(parameterName, parameter.getValue());
            }
            builder.url(urlBuilder.build());
        } else {
            RequestBody body = request.body();
            if (body == null || body.contentLength() == 0) {
                FormBody.Builder bodyBuilder = new FormBody.Builder();
                for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                    bodyBuilder.add(parameter.getKey(), parameter.getValue());
                }
                body = bodyBuilder.build();
            } else if (body instanceof FormBody) {
                FormBody formBody = (FormBody) body;
                FormBody.Builder bodyBuilder = new FormBody.Builder();
                for (int i = 0, size = formBody.size(); i < size; ++i) {
                    String name = formBody.name(i);
                    if (parameters.containsKey(name)) {
                        continue;
                    }
                    bodyBuilder.add(name, formBody.value(i));
                }
                for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                    bodyBuilder.add(parameter.getKey(), parameter.getValue());
                }
                body = bodyBuilder.build();
            } else if (body instanceof MultipartBody) {
                MultipartBody multipartBody = (MultipartBody) body;
                MultipartBody.Builder bodyBuilder = new MultipartBody.Builder(
                        multipartBody.boundary())
                        .setType(multipartBody.type());
                List<MultipartBody.Part> parameterParts = new ArrayList<>();
                for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                    MultipartBody.Part parameterPart = MultipartBody.Part.createFormData(
                            parameter.getKey(), parameter.getValue());
                    parameterParts.add(parameterPart);
                }
                List<String> parameterContentDispositions = new ArrayList<>();
                for (MultipartBody.Part parameterPart : parameterParts) {
                    String parameterContentDisposition = parameterPart.headers().get(
                            Http.Headers.CONTENT_DISPOSITION);
                    parameterContentDispositions.add(parameterContentDisposition);
                }
                for (int i = 0, size = multipartBody.size(); i < size; ++i) {
                    MultipartBody.Part part = multipartBody.part(i);
                    Headers headers = part.headers();
                    if (headers != null) {
                        String contentDisposition = headers.get(Http.Headers.CONTENT_DISPOSITION);
                        if (parameterContentDispositions.contains(contentDisposition)) {
                            continue;
                        }
                    }
                    bodyBuilder.addPart(part);
                }
                for (MultipartBody.Part parameterPart : parameterParts) {
                    bodyBuilder.addPart(parameterPart);
                }
                body = bodyBuilder.build();
            } else {
                LogUtils.wtf("Unknown request body " + body.getClass().getName() + ": " + body);
            }
            builder
                    .method(method, body)
                    .removeHeader(Http.Headers.CONTENT_LENGTH)
                    .header(Http.Headers.CONTENT_LENGTH, String.valueOf(body.contentLength()));
        }
    }

    public static Request addParameters(Request request, Map<String, String> parameters)
            throws IOException {
        Request.Builder builder = request.newBuilder();
        addParameters(builder, request, parameters);
        return builder.build();
    }
}
