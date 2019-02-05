/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.glide.info;

import android.graphics.BitmapFactory;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.SimpleResource;

import java.io.File;
import java.io.IOException;

public class FileImageInfoDecoder implements ResourceDecoder<File, ImageInfo> {

    @Override
    public boolean handles(File source, Options options) {
        return true;
    }

    @Nullable
    @Override
    public Resource<ImageInfo> decode(File source, int width, int height, Options options)
            throws IOException {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(source.getAbsolutePath(), bitmapOptions);
        if (bitmapOptions.outWidth == 0 && bitmapOptions.outHeight == 0
                && bitmapOptions.outMimeType == null) {
            throw new IOException("BitmapFactory.decodeFile() failed");
        }
        return new SimpleResource<>(new ImageInfo(bitmapOptions.outWidth, bitmapOptions.outHeight,
                bitmapOptions.outMimeType));
    }
}
