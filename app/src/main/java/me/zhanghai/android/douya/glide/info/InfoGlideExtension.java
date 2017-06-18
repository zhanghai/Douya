/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.glide.info;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideType;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

@GlideExtension
public class InfoGlideExtension {

    private InfoGlideExtension() {}

    @GlideType(ImageInfo.class)
    public static void asInfo(RequestBuilder<ImageInfo> requestBuilder) {
        requestBuilder
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE));
    }
}
