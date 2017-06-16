/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.glide.progress.okhttp3;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

import java.io.InputStream;

import me.zhanghai.android.douya.glide.progress.ProgressGlideExtension;
import me.zhanghai.android.douya.glide.progress.ProgressListener;
import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * A simple model loader for fetching media over http/https using OkHttp.
 */
public class OkHttpUrlLoader implements ModelLoader<GlideUrl, InputStream> {

    private final Call.Factory client;

    public OkHttpUrlLoader(Call.Factory client) {
        this.client = client;
    }

    @Override
    public boolean handles(GlideUrl url) {
        return true;
    }

    @Override
    public LoadData<InputStream> buildLoadData(GlideUrl model, int width, int height,
                                               Options options) {
        ProgressListener progressListener = options.get(ProgressGlideExtension.OPTION_LISTENER);
        // Otherwise memory leak happens because options is kept in cache.
        // But don't add a null option if it wasn't there so that we won't break the hash for cache.
        // Note that we made ProgressListener.hashCode() return 0 which matches the hash code of
        // null in SimpleArrayMap.hashCode() which is in turn used by Options.hashCode().
        if (progressListener != null) {
            options.set(ProgressGlideExtension.OPTION_LISTENER, null);
        }
        return new LoadData<>(model, new OkHttpStreamFetcher(client, model, progressListener));
    }

    /**
     * The default factory for {@link OkHttpUrlLoader}s.
     */
    public static class Factory implements ModelLoaderFactory<GlideUrl, InputStream> {

        private static volatile Call.Factory internalClient;

        private Call.Factory client;

        private static Call.Factory getInternalClient() {
            if (internalClient == null) {
                synchronized (Factory.class) {
                    if (internalClient == null) {
                        internalClient = new OkHttpClient.Builder()
                                .addNetworkInterceptor(new OkHttpProgressInterceptor())
                                .build();
                    }
                }
            }
            return internalClient;
        }

        /**
         * Constructor for a new Factory that runs requests using a static singleton client.
         */
        public Factory() {
            this.client = getInternalClient();
        }

        @Override
        public ModelLoader<GlideUrl, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new OkHttpUrlLoader(client);
        }

        @Override
        public void teardown() {
            // Do nothing, this instance doesn't own the client.
        }
    }
}
