/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.link;

import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.net.Uri;

import me.zhanghai.android.douya.broadcast.ui.BroadcastActivity;
import me.zhanghai.android.douya.util.UriUtils;

public class DoubanUriHandler {

    private static final String AUTHORITY = "douban.com";

    private enum UriType {
        BROADCAST("status/#");

        String mPath;

        UriType(String path) {
            mPath = path;
        }

        public String getPath() {
            return mPath;
        }
    }

    private static final UriMatcher MATCHER;
    static {
        MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        for (UriType uriType : UriType.values()) {
            MATCHER.addURI(AUTHORITY, uriType.getPath(), uriType.ordinal());
        }
    }

    private DoubanUriHandler() {}

    public static boolean open(Uri uri, Context context) {

        int code = MATCHER.match(uri);
        if (code == UriMatcher.NO_MATCH) {
            return false;
        }
        UriType uriType = UriType.values()[code];

        Intent intent;
        switch (uriType) {
            case BROADCAST:
                intent = BroadcastActivity.makeIntent(context, UriUtils.parseId(uri));
                break;
            default:
                return false;
        }

        context.startActivity(intent);
        return true;
    }

    public static boolean open(String uri, Context context) {
        return open(Uri.parse(uri), context);
    }
}
