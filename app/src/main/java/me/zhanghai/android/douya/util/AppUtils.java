/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;

import me.zhanghai.android.douya.R;

public class AppUtils {

    @Nullable
    public static Activity getActivityFromContext(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            // Can be wrapped by a TintContextWrapper, etc.
            return getActivityFromContext(((ContextWrapper) context).getBaseContext());
        } else {
            return null;
        }
    }

    public static void startActivity(Intent intent, Context context) {
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            ToastUtils.show(R.string.activity_not_found, context);
        }
    }

    public static void startActivityForResult(Activity activity, Intent intent, int requestCode) {
        try {
            activity.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            ToastUtils.show(R.string.activity_not_found, activity);
        }
    }
}
