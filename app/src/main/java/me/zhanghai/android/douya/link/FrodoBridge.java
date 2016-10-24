/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.link;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import me.zhanghai.android.douya.util.AppUtils;

public class FrodoBridge {

    private static final String FRODO_SCHEME = "douban";
    private static final String FACADE_ACTION = "com.douban.frodo";

    private static final String FRODO_PACKAGE_NAME = "com.douban.frodo";

    private static final String SEARCH_CLASS_NAME = ".activity.SearchActivity";
    private static final String SEARCH_EXTRA_QUERY = "query";
    private static final String SEARCH_EXTRA_TYPE = "com.douban.frodo.QUERY_TYPE";
    private static final String SEARCH_EXTRA_ENTRY = "search_entry";

    private static final String SEND_STATUS_CLASS_NAME = ".activity.StatusEditActivity";
    private static final String SEND_STATUS_EXTRA_HASHTAG_NAME = "hashtag_name";

    private FrodoBridge() {}

    public static boolean openUri(Uri uri, Context context) {
        Intent intent = makeFacadeIntent(uri);
        return AppUtils.isIntentHandled(intent, context) && startActivity(intent, context);
    }

    private static Intent makeFacadeIntent(Uri uri) {
        return new Intent(FACADE_ACTION, uri);
    }

    public static boolean openFrodoUri(Uri uri, Context context) {
        return isFrodoUri(uri) && openUri(uri, context);
    }

    public static boolean isFrodoUri(Uri uri) {
        return TextUtils.equals(uri.getScheme(), FRODO_SCHEME);
    }

    public static boolean search(String query, String type, String entry, Context context) {
        return startActivity(makeSearchIntent(query, type, entry), context);
    }

    private static Intent makeSearchIntent(String query, String type, String entry) {
        return makeClassIntent(SEARCH_CLASS_NAME)
                .putExtra(SEARCH_EXTRA_QUERY, query)
                .putExtra(SEARCH_EXTRA_TYPE, type)
                .putExtra(SEARCH_EXTRA_ENTRY, entry);
    }

    public static boolean sendBroadcast(String topic, Context context) {
        return startActivity(makeSendBroadcastIntent(topic), context);
    }

    private static Intent makeSendBroadcastIntent(String hashTagName) {
        return makeClassIntent(SEND_STATUS_CLASS_NAME)
                .putExtra(SEND_STATUS_EXTRA_HASHTAG_NAME, hashTagName);
    }

    private static Intent makeClassIntent(String className) {
        return new Intent()
                .setComponent(new ComponentName(FRODO_PACKAGE_NAME,
                        FRODO_PACKAGE_NAME + className));
    }

    private static boolean startActivity(Intent intent, Context context) {
        try {
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }
}
