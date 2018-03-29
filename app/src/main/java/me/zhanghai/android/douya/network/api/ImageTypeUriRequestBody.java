/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api;

import android.content.ContentResolver;
import android.net.Uri;
import android.text.TextUtils;

import java.io.InputStream;

import javax.annotation.Nullable;

import me.zhanghai.android.douya.util.FileTypeUtils;
import okhttp3.MediaType;

public class ImageTypeUriRequestBody extends UriRequestBody {

    public ImageTypeUriRequestBody(ContentResolver contentResolver, Uri uri) {
        super(contentResolver, uri);
    }

    @Nullable
    @Override
    public MediaType contentType() {

        MediaType superType = super.contentType();
        if (superType != null) {
            return superType;
        }

        String mimeType = FileTypeUtils.getImageMimeType(mUri.getPath());
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
