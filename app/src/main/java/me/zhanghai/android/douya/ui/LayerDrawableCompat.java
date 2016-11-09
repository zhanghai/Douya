/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

public class LayerDrawableCompat extends LayerDrawable {

    public LayerDrawableCompat(Drawable[] layers) {
        super(layers);
    }

    @Override
    public boolean isStateful() {
        for (int i = 0, size = getNumberOfLayers(); i < size; ++i) {
            if (getDrawable(i).isStateful()) {
                return true;
            }
        }
        return false;
    }
}
