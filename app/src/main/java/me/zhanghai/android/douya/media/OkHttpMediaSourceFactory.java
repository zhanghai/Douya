/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.media;

import android.net.Uri;

import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.TransferListener;

import okhttp3.Call;
import okhttp3.OkHttpClient;

public class OkHttpMediaSourceFactory {

    private ExtractorMediaSource.Factory mFactory;

    public OkHttpMediaSourceFactory(Call.Factory callFactory, String userAgent,
                                    TransferListener<? super DataSource> listener) {
        OkHttpDataSourceFactory dataSourceFactory = new OkHttpDataSourceFactory(callFactory,
                userAgent, listener);
        mFactory = new ExtractorMediaSource.Factory(dataSourceFactory);
    }

    public OkHttpMediaSourceFactory() {
        this(new OkHttpClient(), null, null);
    }

    public MediaSource create(Uri uri) {
        return mFactory.createMediaSource(uri);
    }
}
