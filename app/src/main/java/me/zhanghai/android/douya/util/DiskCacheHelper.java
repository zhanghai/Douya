/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.content.Context;
import android.os.Handler;

import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import me.zhanghai.android.douya.BuildConfig;

public class DiskCacheHelper {

    // 2M
    private static final int MAX_DISK_CACHE_BYTES = 2 * 1024 * 1024;

    private static final Object DISK_CACHE_LOCK = new Object();
    private static volatile DiskCache sDiskCache;

    private static final ExecutorService sExecutorService = Executors.newSingleThreadExecutor();

    private DiskCacheHelper() {}

    public static DiskCache get(Context context) {
        if (sDiskCache == null) {
            synchronized (DISK_CACHE_LOCK) {
                if (sDiskCache == null) {
                    sDiskCache = DiskCache.open(new File(context.getCacheDir(),
                            BuildConfig.APPLICATION_ID), 0, MAX_DISK_CACHE_BYTES);
                }
            }
        }
        return sDiskCache;
    }

    private static void executeAsync(Runnable runnable) {
        try {
            sExecutorService.execute(runnable);
        } catch (RejectedExecutionException e) {
            e.printStackTrace();
        }
    }

    private static <T> void deliverValue(Handler handler, final Callback<T> callback,
                                         final T value) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onValue(value);
            }
        });
    }

    public static void getString(final String key, final Handler handler,
                                 final Callback<String> callback, final Context context) {
        executeAsync(new Runnable() {
            @Override
            public void run() {
                DiskCache diskCache = get(context);
                deliverValue(handler, callback, diskCache != null ? diskCache.getString(key)
                        : null);
            }
        });
    }

    public static <T> void getGson(final String key, final Type type, final Handler handler,
                                   final Callback<T> callback, final Context context) {
        executeAsync(new Runnable() {
            @Override
            public void run() {
                DiskCache diskCache = get(context);
                deliverValue(handler, callback, diskCache != null ? diskCache.<T>getGson(key, type)
                        : null);
            }
        });
    }

    public static <T> void getGson(String key, TypeToken<T> typeToken, Handler handler,
                                   Callback<T> callback, Context context) {
        getGson(key, typeToken.getType(), handler, callback, context);
    }

    public static void putBytes(final String key, final byte[] value, final Context context) {
        executeAsync(new Runnable() {
            @Override
            public void run() {
                DiskCache diskCache = get(context);
                if (diskCache != null) {
                    diskCache.putBytes(key, value);
                }
            }
        });
    }

    public static void putString(final String key, final String value, final Context context) {
        executeAsync(new Runnable() {
            @Override
            public void run() {
                DiskCache diskCache = get(context);
                if (diskCache != null) {
                    diskCache.putString(key, value);
                }
            }
        });
    }

    public static <T> void putGson(final String key, final T value, final Type type,
                                   final Context context) {
        executeAsync(new Runnable() {
            @Override
            public void run() {
                DiskCache diskCache = get(context);
                if (diskCache != null) {
                    diskCache.putGson(key, value, type);
                }
            }
        });
    }

    public static <T> void putGson(String key, T value, TypeToken<T> typeToken, Context context) {
        putGson(key, value, typeToken.getType(), context);
    }

    public static void remove(final String key, final Handler handler,
                              final Callback<Boolean> callback, final Context context) {
        executeAsync(new Runnable() {
            @Override
            public void run() {
                DiskCache diskCache = get(context);
                if (diskCache != null) {
                    deliverValue(handler, callback, diskCache.remove(key));
                }
            }
        });
    }

    public static void remove(final String key, final Context context) {
        executeAsync(new Runnable() {
            @Override
            public void run() {
                DiskCache diskCache = get(context);
                if (diskCache != null) {
                    diskCache.remove(key);
                }
            }
        });
    }

    public static void delete(final Context context) {
        executeAsync(new Runnable() {
            @Override
            public void run() {
                DiskCache diskCache = get(context);
                if (diskCache != null) {
                    diskCache.delete();
                }
            }
        });
    }

    public static void close(final Context context) {
        executeAsync(new Runnable() {
            @Override
            public void run() {
                DiskCache diskCache = get(context);
                if (diskCache != null) {
                    diskCache.close();
                }
            }
        });
    }
}
