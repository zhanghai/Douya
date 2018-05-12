/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

public class UriUtils {

    public static boolean isWebScheme(Uri uri) {
        String scheme = uri.getScheme();
        if (TextUtils.isEmpty(scheme)) {
            return false;
        }
        switch (uri.getScheme()) {
            case "http":
            case "https":
                return true;
            default:
                return false;
        }
    }

    public static String getDisplayName(Uri uri, ContentResolver contentResolver) {
        String displayName = null;
        try (Cursor cursor = contentResolver.query(uri, null, null, null, null)) {
            if (cursor != null && cursor.getCount() > 0) {
                int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (columnIndex != -1) {
                    cursor.moveToFirst();
                    if (!cursor.isNull(columnIndex)) {
                        displayName = cursor.getString(columnIndex);
                    }
                }
            }
        }
        if (TextUtils.isEmpty(displayName)) {
            displayName = uri.getLastPathSegment();
        }
        return displayName;
    }

    public static String getDisplayName(Uri uri, Context context) {
        return getDisplayName(uri, context.getContentResolver());
    }

    public static long getSize(Uri uri, ContentResolver contentResolver) {
        try (Cursor cursor = contentResolver.query(uri, null, null, null, null)) {
            if (cursor != null && cursor.getCount() > 0) {
                int columnIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (columnIndex != -1) {
                    cursor.moveToFirst();
                    if (!cursor.isNull(columnIndex)) {
                        return cursor.getLong(columnIndex);
                    }
                }
            }
        }
        return -1;
    }

    public static long getSize(Uri uri, Context context) {
        return getSize(uri, context.getContentResolver());
    }

    public static String getType(Uri uri, ContentResolver contentResolver) {
        String type = contentResolver.getType(uri);
        if (TextUtils.isEmpty(type)) {
            String extension = FileNameUtils.getExtension(uri.getLastPathSegment());
            if (!TextUtils.isEmpty(extension)) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }
        }
        if (TextUtils.isEmpty(type)) {
            String displayName = getDisplayName(uri, contentResolver);
            String extension = FileNameUtils.getExtension(displayName);
            if (!TextUtils.isEmpty(extension)) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }
        }
        return !TextUtils.isEmpty(type) ? type : null;
    }

    public static String getType(Uri uri, Context context) {
        return getType(uri, context.getContentResolver());
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
