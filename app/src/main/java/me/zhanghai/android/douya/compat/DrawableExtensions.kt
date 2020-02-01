/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.compat

import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.DrawableCompat

var Drawable.layoutDirectionCompat: Int
    get() = DrawableCompat.getLayoutDirection(this)
    set(value) { DrawableCompat.setLayoutDirection(this, value) }
