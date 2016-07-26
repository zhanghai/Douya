/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.link;

import android.content.Context;
import android.net.Uri;

public class UriHandler {

    private UriHandler() {}

    public static void open(Uri uri, Context context) {
        if (DoubanUriHandler.open(uri, context)) {
            return;
        }
        if (FrodoBridge.openFrodoUri(uri, context)) {
            return;
        }
        UrlHandler.open(uri, context);
    }

    public static void open(String url, Context context) {
        open(Uri.parse(url), context);
    }
}
