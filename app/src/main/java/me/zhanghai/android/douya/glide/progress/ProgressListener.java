/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.glide.progress;

public interface ProgressListener {

    void onProgress(long bytesRead, long contentLength, boolean done);
}
