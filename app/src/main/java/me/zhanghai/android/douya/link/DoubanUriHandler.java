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
import me.zhanghai.android.douya.profile.ui.ProfileActivity;
import me.zhanghai.android.douya.util.UriUtils;

public class DoubanUriHandler {

    private static final String AUTHORITY = "www.douban.com";
    private static final String AUTHORITY_FRODO = "douban.com";

    private enum UriType {

        BROADCAST("people/*/status/#"),
        BROADCAST_FRODO(AUTHORITY_FRODO, "status/#"),
        USER("people/*");

        String mAuthority;
        String mPath;

        UriType(String authority, String path) {
            mAuthority = authority;
            mPath = path;
        }

        UriType(String path) {
            this(AUTHORITY, path);
        }

        public String getAuthority() {
            return mAuthority;
        }

        public String getPath() {
            return mPath;
        }
    }

    private static final UriMatcher MATCHER;
    static {
        MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        for (UriType uriType : UriType.values()) {
            MATCHER.addURI(uriType.getAuthority(), uriType.getPath(), uriType.ordinal());
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
            case BROADCAST_FRODO:
                intent = BroadcastActivity.makeIntent(context, UriUtils.parseId(uri));
                break;
//            case USER:
//                intent = ProfileActivity.makeIntent(context, uri.getLastPathSegment());
//                break;
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
