/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.glide.download;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideType;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

@GlideExtension
public class DownloadGlideExtension {

    private static final RequestOptions REQUEST_OPTIONS_DOWNLOAD_ONLY_DEFAULT_PRIORITY =
            RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.DATA)
                    //.priority(Priority.LOW)
                    .skipMemoryCache(true);

    private DownloadGlideExtension() {}

    /**
     * @see RequestManager#downloadOnly()
     */
    @GlideType(File.class)
    public static void downloadOnlyDefaultPriority(RequestBuilder<File> requestBuilder) {
        requestBuilder.apply(REQUEST_OPTIONS_DOWNLOAD_ONLY_DEFAULT_PRIORITY);
    }
}
