/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.gallery.app;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.MediaStore;

import java.io.File;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.ToastUtils;

public class SaveImageService extends IntentService {

    private static final String KEY_PREFIX = SaveImageService.class.getName() + '.';

    private static final String EXTRA_FILE = KEY_PREFIX + "file";

    private Handler mServiceHandler;

    public static void start(File file, Context context) {
        Intent intent = new Intent(context, SaveImageService.class);
        intent.putExtra(EXTRA_FILE, file);
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

        File file = (File) intent.getSerializableExtra(EXTRA_FILE);
        saveImage(file);
    }

    private void saveImage(File file) {
        try {
            // TODO: Save to Pictures/Douya?
            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(),
                    file.getName(), null);
            postToast(R.string.gallery_save_successful);
        } catch (Exception e) {
            e.printStackTrace();
            postToast(R.string.gallery_save_failed);
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
