/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import java.io.InputStream;

import me.zhanghai.android.douya.util.FileTypeUtils;
import me.zhanghai.android.douya.util.UriUtils;
import okhttp3.MediaType;

public class ImageTypeUriRequestBody extends UriRequestBody {

    public ImageTypeUriRequestBody(Uri uri, ContentResolver contentResolver) {
        super(uri, contentResolver);
    }

    public ImageTypeUriRequestBody(Uri uri, Context context) {
        super(uri, context);
    }

    @Nullable
    @Override
    public MediaType contentType() {

        MediaType superType = super.contentType();
        if (superType != null) {
            return superType;
        }

        String mimeType = UriUtils.getType(mUri, mContentResolver);
        if (TextUtils.isEmpty(mimeType)) {
            try (InputStream inputStream = mContentResolver.openInputStream(mUri)) {
                mimeType = FileTypeUtils.getImageMimeType(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(mimeType)) {
            return MediaType.parse(mimeType);
        }

        return null;
    }
}
