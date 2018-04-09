/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;

import java.io.IOException;
import java.security.Key;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import me.zhanghai.android.douya.network.Http;
import me.zhanghai.android.douya.network.api.credential.ApiCredential;
import me.zhanghai.android.douya.util.MoreTextUtils;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * See ApiSignatureHelper and ApiSignatureInterceptor in Frodo.
 */
public class FrodoSignatureInterceptor implements Interceptor {

    private static final String ALGORITHM_HMAC_SHA1 = "HmacSHA1";

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        String host = request.url().host();
        if (!ApiContract.Request.Frodo.SIGNATURE_HOSTS.contains(host)) {
            return chain.proceed(request);
        }
        String timestamp = makeTimestamp();
        String signature = makeSignature(request, timestamp);
        if (TextUtils.isEmpty(signature)) {
            return chain.proceed(request);
        }
        if (TextUtils.equals(request.method(), Http.Methods.GET)) {
            request = request.newBuilder()
                    .url(request.url().newBuilder()
                            .setQueryParameter(ApiContract.Request.Frodo.SIG, signature)
                            .setQueryParameter(ApiContract.Request.Frodo.TS, timestamp)
                            .build())
                    .build();
        } else {
            RequestBody body = request.body();
            if (body instanceof FormBody) {
                FormBody formBody = (FormBody) body;
                FormBody.Builder builder = new FormBody.Builder();
                for (int i = 0, size = formBody.size(); i < size; ++i) {
                    String name = formBody.name(i);
                    if (MoreTextUtils.equalsAny(name, ApiContract.Request.Frodo.SIG,
                            ApiContract.Request.Frodo.TS)) {
                        continue;
                    }
                    builder.add(name, formBody.value(i));
                }
                builder.add(ApiContract.Request.Frodo.SIG, signature);
                builder.add(ApiContract.Request.Frodo.TS, timestamp);
                body = builder.build();
                // Added sanity check because Frodo is assuming POST.
                if (!TextUtils.equals(request.method(), Http.Methods.POST)) {
                    throw new IllegalArgumentException("Method must be GET or POST for " +
                            FrodoSignatureInterceptor.class.getSimpleName());
                }
                request = request.newBuilder()
                        .post(body)
                        .removeHeader(Http.Headers.CONTENT_LENGTH)
                        .header(Http.Headers.CONTENT_LENGTH, String.valueOf(body.contentLength()))
                        .build();
            } else if (body instanceof MultipartBody) {
                MultipartBody multipartBody = (MultipartBody) body;
                MultipartBody.Builder builder = new MultipartBody.Builder(multipartBody.boundary())
                        .setType(multipartBody.type());
                for (int i = 0, size = multipartBody.size(); i < size; ++i) {
                    MultipartBody.Part part = multipartBody.part(i);
                    Headers headers = part.headers();
                    if (headers != null) {
                        String contentDisposition = headers.get(Http.Headers.CONTENT_DISPOSITION);
                        if (MoreTextUtils.containsAny(contentDisposition,
                                ApiContract.Request.Frodo.SIG, ApiContract.Request.Frodo.TS)) {
                            continue;
                        }
                    }
                    builder.addPart(part);
                }
                builder.addFormDataPart(ApiContract.Request.Frodo.SIG, signature);
                builder.addFormDataPart(ApiContract.Request.Frodo.TS, timestamp);
                body = builder.build();
                // Added sanity check because Frodo is assuming POST.
                if (!TextUtils.equals(request.method(), Http.Methods.POST)) {
                    throw new IllegalArgumentException("Method must be GET or POST for " +
                            FrodoSignatureInterceptor.class.getSimpleName());
                }
                request = request.newBuilder()
                        .post(body)
                        .removeHeader(Http.Headers.CONTENT_LENGTH)
                        .header(Http.Headers.CONTENT_LENGTH, String.valueOf(body.contentLength()))
                        .build();
            }
        }
        return chain.proceed(request);
    }

    private String makeTimestamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    private String makeSignature(Request request, String timestamp) {

        String key = ApiCredential.Frodo.SECRET;
        if (TextUtils.isEmpty(key)) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        builder.append(request.method());
        String path = request.url().encodedPath();
        path = Uri.decode(path);
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        path = Uri.encode(path);
        builder.append("&").append(path);
        String authorization = request.header(Http.Headers.AUTHORIZATION);
        if (!TextUtils.isEmpty(authorization)) {
            String authToken = authorization.substring(7);
            if (!TextUtils.isEmpty(authToken)) {
                builder.append("&").append(authToken);
            }
        }
        builder.append("&").append(timestamp);

        String signature = builder.toString();
        signature = hmacSha1(key, signature);
        return signature;
    }

    // Would have used getBytes(StandardCharsetsCompat.UTF_8) if not conforming to Frodo.
    private String hmacSha1(String key, String input) {
        try {
            Key keySpec = new SecretKeySpec(key.getBytes(), ALGORITHM_HMAC_SHA1);
            Mac mac = Mac.getInstance(ALGORITHM_HMAC_SHA1);
            mac.init(keySpec);
            byte[] result = mac.doFinal(input.getBytes());
            return Base64.encodeToString(result, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
