/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

public class UriUtils {

    public static String getType(Uri uri, Context context) {
        String type = context.getContentResolver().getType(uri);
        if (TextUtils.isEmpty(type)) {
            String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            if (!TextUtils.isEmpty(extension)) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }
        }
        return !TextUtils.isEmpty(type) ? type : null;
    }

    public static long parseId(Uri uri) {
        String last = uri.getLastPathSegment();
        return last == null ? -1 : Long.parseLong(last);
    }

    public static Uri.Builder appendId(Uri.Builder builder, long id) {
        return builder.appendEncodedPath(String.valueOf(id));
    }

    public static Uri withAppendedId(Uri contentUri, long id) {
        return appendId(contentUri.buildUpon(), id).build();
    }

    public static Uri withoutQueryAndFragment(Uri uri) {
        return uri.buildUpon()
                .clearQuery()
                .fragment(null)
                .build();
    }
}
