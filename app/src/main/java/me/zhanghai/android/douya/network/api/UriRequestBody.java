/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;

import me.zhanghai.android.douya.util.UriUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;

public class UriRequestBody extends RequestBody {

    protected Uri mUri;
    protected ContentResolver mContentResolver;

    protected long mSize;

    public UriRequestBody(Uri uri, ContentResolver contentResolver) {

        mUri = uri;
        mContentResolver = contentResolver;

        mSize = UriUtils.getSize(mUri, mContentResolver);
    }

    public UriRequestBody(Uri uri, Context context) {
        this(uri, context.getContentResolver());
    }

    @Nullable
    @Override
    public MediaType contentType() {
        String type = mContentResolver.getType(mUri);
        if (TextUtils.isEmpty(type)) {
            return null;
        }
        return MediaType.parse(type);
    }

    @Override
    public long contentLength() {
        return mSize;
    }

    @Override
    public void writeTo(@NonNull BufferedSink sink) throws IOException {
        try (InputStream inputStream = mContentResolver.openInputStream(mUri)) {
            if (inputStream == null) {
                throw new IOException("Failed to open input stream for Uri: " + mUri);
            }
            sink.writeAll(Okio.source(inputStream));
        }
    }
}
