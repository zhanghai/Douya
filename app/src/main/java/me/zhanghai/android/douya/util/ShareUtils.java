/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.content.Context;

import me.zhanghai.android.douya.network.api.info.UrlGettable;

public class ShareUtils {

    private ShareUtils() {}

    public static void share(String url, Context context) {
        AppUtils.startActivityWithChooser(IntentUtils.makeSendText(url), context);
    }

    public static void share(UrlGettable urlGettable, Context context) {
        share(urlGettable.getUrl(), context);
    }
}
