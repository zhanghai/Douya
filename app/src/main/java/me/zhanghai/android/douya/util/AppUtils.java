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
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;

import me.zhanghai.android.douya.R;

public class AppUtils {

    private AppUtils() {}

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

    public static boolean isIntentHandled(Intent intent, Context context) {
        return intent.resolveActivity(context.getPackageManager()) != null;
    }

    // From http://developer.android.com/training/implementing-navigation/ancestral.html#NavigateUp .
    public static void navigateUp(Activity activity, Bundle extras) {
        Intent upIntent = NavUtils.getParentActivityIntent(activity);
        if (upIntent != null) {
            if (extras != null) {
                upIntent.putExtras(extras);
            }
            if (NavUtils.shouldUpRecreateTask(activity, upIntent)) {
                // This activity is NOT part of this app's task, so create a new task
                // when navigating up, with a synthesized back stack.
                TaskStackBuilder.create(activity)
                        // Add all of this activity's parents to the back stack.
                        .addNextIntentWithParentStack(upIntent)
                        // Navigate up to the closest parent.
                        .startActivities();
            } else {
                // This activity is part of this app's task, so simply
                // navigate up to the logical parent activity.
                // According to http://stackoverflow.com/a/14792752/2420519
                //NavUtils.navigateUpTo(activity, upIntent);
                upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(upIntent);
            }
        }
        activity.finish();
    }

    public static void navigateUp(Activity activity) {
        navigateUp(activity, null);
    }

    private static final Handler sMainHandler = new Handler(Looper.getMainLooper());

    public static void runOnUiThread(Runnable runnable) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            runnable.run();
        } else {
            sMainHandler.post(runnable);
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
