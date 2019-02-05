/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import android.net.Uri;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;

import java.io.IOException;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import me.zhanghai.android.douya.network.Http;
import me.zhanghai.android.douya.network.api.credential.ApiCredential;
import me.zhanghai.android.douya.network.api.util.InterceptorUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
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
        Map<String, String> parameters = new HashMap<>();
        parameters.put(ApiContract.Request.Frodo.SIG, signature);
        parameters.put(ApiContract.Request.Frodo.TS, timestamp);
        request = InterceptorUtils.addParameters(request, parameters);
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
