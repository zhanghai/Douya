/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.webkit.MimeTypeMap;

import com.bumptech.glide.load.ImageHeaderParser;
import com.bumptech.glide.load.resource.bitmap.DefaultImageHeaderParser;

import java.io.IOException;
import java.io.InputStream;

public class FileTypeUtils {

    private static final ImageHeaderParser sImageHeaderParser = new DefaultImageHeaderParser();

    private FileTypeUtils() {}

    public static String getImageMimeType(InputStream inputStream, String defaultMimeType)
            throws IOException {
        ImageHeaderParser.ImageType imageType = sImageHeaderParser.getType(inputStream);
        String extension;
        switch (imageType) {
            case GIF:
                extension = "gif";
                break;
            case JPEG:
                extension = "jpg";
                break;
            case PNG_A:
            case PNG:
                extension = "png";
                break;
            case WEBP_A:
            case WEBP:
                extension = "webp";
                break;
            default:
                return defaultMimeType;
        }
        // See FileProvider.getType(Uri)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    public static String getImageMimeType(InputStream inputStream) throws IOException {
        return getImageMimeType(inputStream, null);
    }
}
