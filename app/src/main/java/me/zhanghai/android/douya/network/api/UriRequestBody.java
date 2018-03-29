/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nullable;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;

public class UriRequestBody extends RequestBody {

    protected ContentResolver mContentResolver;
    protected Uri mUri;

    public UriRequestBody(ContentResolver contentResolver, Uri uri) {
        mContentResolver = contentResolver;
        mUri = uri;
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
    public void writeTo(@NonNull BufferedSink sink) throws IOException {
        try (InputStream inputStream = mContentResolver.openInputStream(mUri)) {
            if (inputStream == null) {
                throw new IOException("Failed to open input stream for Uri: " + mUri);
            }
            sink.writeAll(Okio.source(inputStream));
        }
    }
}
