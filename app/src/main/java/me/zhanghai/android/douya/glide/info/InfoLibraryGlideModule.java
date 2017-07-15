/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.glide.info;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.LibraryGlideModule;

import java.io.File;

@GlideModule
public final class InfoLibraryGlideModule extends LibraryGlideModule {

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        registry.prepend(File.class, ImageInfo.class, new FileImageInfoDecoder());
    }
}
