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
import me.zhanghai.android.douya.broadcast.ui.BroadcastListActivity;
import me.zhanghai.android.douya.followship.ui.FollowerListActivity;
import me.zhanghai.android.douya.followship.ui.FollowingListActivity;
import me.zhanghai.android.douya.profile.ui.ProfileActivity;
import me.zhanghai.android.douya.util.UriUtils;

public class DoubanUriHandler {

    private static final String AUTHORITY = "www.douban.com";
    private static final String AUTHORITY_FRODO = "douban.com";

    private enum UriType {

        USER_BROADCAST_LIST("people/*/statuses"),
        TOPIC_BROADCAST_LIST("update/topic/*"),
        BROADCAST("people/*/status/#"),
        BROADCAST_FRODO(AUTHORITY_FRODO, "status/#"),
        USER("people/*"),
        USER_FOLLOWER_LIST("people/*/followers"),
        USER_FOLLOWER_LIST_FRODO(AUTHORITY_FRODO, "user/*/follower"),
        USER_FOLLOWING_LIST("people/*/followings"),
        USER_FOLLOWING_LIST_FRODO(AUTHORITY_FRODO, "user/*/following");

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
            case USER_BROADCAST_LIST:
                intent = BroadcastListActivity.makeIntent(uri.getPathSegments().get(1), context);
                break;
            case TOPIC_BROADCAST_LIST:
                intent = BroadcastListActivity.makeTopicIntent(uri.getLastPathSegment(), context);
                break;
            case BROADCAST:
            case BROADCAST_FRODO:
                intent = BroadcastActivity.makeIntent(UriUtils.parseId(uri), context);
                break;
            case USER:
                intent = ProfileActivity.makeIntent(uri.getLastPathSegment(), context);
                break;
            case USER_FOLLOWER_LIST:
            case USER_FOLLOWER_LIST_FRODO:
                intent = FollowerListActivity.makeIntent(uri.getPathSegments().get(1), context);
                break;
            case USER_FOLLOWING_LIST:
            case USER_FOLLOWING_LIST_FRODO:
                intent = FollowingListActivity.makeIntent(uri.getPathSegments().get(1), context);
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
