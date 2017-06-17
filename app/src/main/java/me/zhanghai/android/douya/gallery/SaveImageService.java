/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.gallery;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.MediaStore;

import com.bumptech.glide.request.FutureTarget;

import java.io.File;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.glide.GlideApp;
import me.zhanghai.android.douya.util.ToastUtils;

public class SaveImageService extends IntentService {

    private static final String KEY_PREFIX = SaveImageService.class.getName() + '.';

    private static final String EXTRA_URL = KEY_PREFIX + "url";

    private Handler mServiceHandler;

    public static void start(String url, Context context) {
        Intent intent = new Intent(context, SaveImageService.class);
        intent.putExtra(EXTRA_URL, url);
        context.startService(intent);
    }

    public SaveImageService() {
        super(SaveImageService.class.getSimpleName());
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mServiceHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent == null) {
            return;
        }

        String url = intent.getStringExtra(EXTRA_URL);
        saveImage(url);
    }

    private void saveImage(String url) {
        FutureTarget<File> futureTarget = GlideApp.with(getApplicationContext())
                .downloadOnly()
                .load(url)
                .submit();
        try {
            File file = futureTarget.get();
            // TODO: Request permission: android.permission.WRITE_EXTERNAL_STORAGE
            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(),
                    file.getName(), null);
            postToast(R.string.gallery_save_successful);
        } catch (Exception e) {
            e.printStackTrace();
            postToast(R.string.gallery_save_failed);
        } finally {
            futureTarget.cancel(true);
        }
    }

    private void postToast(final int resId) {
        mServiceHandler.post(new Runnable() {
            @Override
            public void run() {
                ToastUtils.show(resId, SaveImageService.this);
            }
        });
    }
}
