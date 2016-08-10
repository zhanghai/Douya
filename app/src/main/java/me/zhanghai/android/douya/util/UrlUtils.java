/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;

import org.chromium.customtabsclient.CustomTabsActivityHelper;

import me.zhanghai.android.customtabshelper.CustomTabsHelperFragment;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.ui.WebViewActivity;

public class UrlUtils {

    private UrlUtils() {}

    public static void openWithWebViewActivity(Uri uri, Context context) {
        context.startActivity(WebViewActivity.makeIntent(uri, context));
    }

    public static void openWithIntent(Uri uri, Context context) {
        Intent intent = IntentUtils.makeView(uri);
        AppUtils.startActivity(intent, context);
    }

    public static void openWithIntent(String url, Context context) {
        openWithIntent(Uri.parse(url), context);
    }

    public static void openWithCustomTabs(Uri uri,
                                          CustomTabsActivityHelper.CustomTabsFallback fallback,
                                          Activity activity) {
        CustomTabsHelperFragment.open(activity, makeCustomTabsIntent(activity), uri, fallback);
    }

    private static CustomTabsIntent makeCustomTabsIntent(Context context) {
        return new CustomTabsIntent.Builder()
                .addDefaultShareMenuItem()
                .enableUrlBarHiding()
                .setToolbarColor(ViewUtils.getColorFromAttrRes(R.attr.colorPrimary, 0, context))
                .setShowTitle(true)
                .build();
    }
}
