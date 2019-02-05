/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.glide.progress.okhttp3;

import androidx.annotation.NonNull;

import java.io.IOException;

import me.zhanghai.android.douya.glide.progress.ProgressListener;
import me.zhanghai.android.douya.util.AppUtils;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class OkHttpProgressInterceptor implements Interceptor {

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(chain.request());
        Object requestTag = request.tag();
        if (!(requestTag instanceof ProgressListener)) {
            return response;
        }
        ProgressListener progressListener = (ProgressListener) requestTag;
        return response.newBuilder()
                .body(new ProgressResponseBody(response.body(), progressListener))
                .build();
    }

    private static class ProgressResponseBody extends ResponseBody {

        private ResponseBody responseBody;
        private ProgressListener progressListener;
        private BufferedSource bufferedSource;

        public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
            this.responseBody = responseBody;
            this.progressListener = progressListener;
        }

        @Override
        public MediaType contentType() {
            return responseBody.contentType();
        }

        @Override
        public long contentLength() {
            return responseBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(new ProgressSource(responseBody.source()));
            }
            return bufferedSource;
        }

        private class ProgressSource extends ForwardingSource {

            private long totalBytesRead;

            public ProgressSource(Source source) {
                super(source);
            }

            @Override
            public long read(@NonNull Buffer sink, long byteCount) throws IOException {
                final long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                AppUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressListener.onProgress(totalBytesRead, responseBody.contentLength(),
                                bytesRead == -1);
                    }
                });
                return bytesRead;
            }
        }
    }
}
