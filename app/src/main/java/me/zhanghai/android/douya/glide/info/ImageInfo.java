/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.glide.info;

public class ImageInfo {

    public int width;
    public int height;
    public String mimeType;

    public ImageInfo(int width, int height, String mimeType) {
        this.width = width;
        this.height = height;
        this.mimeType = mimeType;
    }
}
