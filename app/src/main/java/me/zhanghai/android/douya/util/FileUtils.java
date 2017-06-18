/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import java.io.File;

import me.zhanghai.android.douya.BuildConfig;

public class FileUtils {

    private FileUtils() {}

    public static Uri getContentUri(File file, Context context) {
        return FileProvider.getUriForFile(context, BuildConfig.FILE_PROVIDIER_AUTHORITY, file);
    }
}
