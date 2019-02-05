/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.glide.progress.okhttp3;

import androidx.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.util.ContentLengthInputStream;
import com.bumptech.glide.util.Synthetic;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import me.zhanghai.android.douya.glide.progress.ProgressListener;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Fetches an {@link InputStream} using the okhttp library.
 */
public class OkHttpStreamFetcher implements DataFetcher<InputStream> {

    private static final String TAG = "OkHttpFetcher";
    private final Call.Factory client;
    private final GlideUrl url;
    private final ProgressListener progressListener;
    @Synthetic InputStream stream;
    @Synthetic ResponseBody responseBody;
    private volatile Call call;

    public OkHttpStreamFetcher(Call.Factory client, GlideUrl url,
                               ProgressListener progressListener) {
        this.client = client;
        this.url = url;
        this.progressListener = progressListener;
    }

    @Override
    public void loadData(Priority priority, final DataCallback<? super InputStream> callback) {

        Request.Builder requestBuilder = new Request.Builder().url(url.toStringUrl());
        for (Map.Entry<String, String> headerEntry : url.getHeaders().entrySet()) {
            String key = headerEntry.getKey();
            requestBuilder.addHeader(key, headerEntry.getValue());
        }
        Request request = requestBuilder
                .tag(progressListener)
                .build();

        call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "OkHttp failed to obtain result", e);
                }
                callback.onLoadFailed(e);
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response)
                    throws IOException {
                responseBody = response.body();
                if (response.isSuccessful()) {
                    long contentLength = responseBody.contentLength();
                    stream = ContentLengthInputStream.obtain(responseBody.byteStream(),
                            contentLength);
                } else if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "OkHttp got error response: " + response.code() + ", " +
                            response.message());
                }
                callback.onDataReady(stream);
            }
        });
    }

    @Override
    public void cleanup() {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {
            // Ignored
        }
        if (responseBody != null) {
            responseBody.close();
        }
    }

    @Override
    public void cancel() {
        Call local = call;
        if (local != null) {
            local.cancel();
        }
    }

    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @Override
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }
}
