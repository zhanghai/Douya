/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.zhanghai.android.douya.BuildConfig;
import me.zhanghai.android.douya.app.ImageTypeFileProvider;

public class FileUtils {

    // Should be kept in sync with file_provider_paths.xml .
    private static final String SUB_DIRECTORY_NAME = "Douya";

    private FileUtils() {}

    public static void copy(File input, File output) throws IOException {
        try (InputStream inputStream = new FileInputStream(input);
             OutputStream outputStream = new FileOutputStream(output)) {
            IoUtils.inputStreamToOutputStream(inputStream, outputStream);
        }
    }

    public static File makeCaptureImageOutputFile() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String fileName = "IMG_" + timestamp + ".jpg";
        return makeSaveImageOutputFile(fileName);
    }

    public static File makeSaveImageOutputFile(String fileName) {
        File directory = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), SUB_DIRECTORY_NAME);
        //noinspection ResultOfMethodCallIgnored
        directory.mkdirs();
        return new File(directory, fileName);
    }

    public static Uri getContentUri(File file, Context context) {
        return ImageTypeFileProvider.getUriForFile(context, BuildConfig.FILE_PROVIDIER_AUTHORITY,
                file);
    }
}
