/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya;

import android.app.Application;

import com.bumptech.glide.request.target.ViewTarget;
import com.facebook.stetho.Stetho;
import com.jakewharton.threetenabp.AndroidThreeTen;

import me.zhanghai.android.douya.fabric.FabricUtils;
import me.zhanghai.android.douya.util.NightModeHelper;

public class DouyaApplication extends Application {

    private static DouyaApplication sInstance;

    public DouyaApplication() {
        sInstance = this;
    }

    public static DouyaApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        NightModeHelper.setup(this);

        AndroidThreeTen.init(this);
        FabricUtils.init(this);
        ViewTarget.setTagId(R.id.glide_view_target_tag);
        Stetho.initializeWithDefaults(this);
    }
}
