/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

public interface SizedImageItem extends ImageItem {

    int getLargeWidth();

    int getLargeHeight();

    int getMediumWidth();

    int getMediumHeight();

    int getSmallWidth();

    int getSmallHeight();
}
