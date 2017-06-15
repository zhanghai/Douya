/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.glide.progress;

import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.request.RequestOptions;

@GlideExtension
public class ProgressGlideExtension {

    public static final Option<ProgressListener> OPTION_LISTENER = Option.memory(
            ProgressGlideExtension.class.getName());

    private ProgressGlideExtension() {}

    @GlideOption
    public static void progressListener(RequestOptions options, ProgressListener listener) {
        // RequestOptions is unhappy with null values.
        //options.set(OPTION_LISTENER, listener);
        options.getOptions().set(OPTION_LISTENER, listener);
    }
}
