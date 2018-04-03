/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.link;

import android.content.Context;

import me.zhanghai.android.douya.BuildConfig;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.broadcast.ui.SendBroadcastActivity;
import me.zhanghai.android.douya.settings.info.Settings;
import me.zhanghai.android.douya.util.ToastUtils;

public class NotImplementedManager {

    private NotImplementedManager() {}

    public static void editProfile(Context context) {
        UrlHandler.open("https://www.douban.com/accounts/", context);
    }

    public static void openDoumail(Context context) {
        UrlHandler.open("https://www.douban.com/doumail/", context);
    }

    public static void sendDoumail(String userIdOrUid, Context context) {
        UrlHandler.open("https://www.douban.com/doumail/write?to=" + userIdOrUid, context);
    }

    public static void openSearch(Context context) {
        if (Settings.PROGRESSIVE_THIRD_PARTY_APP.getValue()
                && FrodoBridge.search(null, null, null, context)) {
            return;
        }
        UrlHandler.open("https://www.douban.com/search", context);
    }

    public static void showNotYetImplementedToast(Context context) {
        ToastUtils.show(R.string.not_yet_implemented, context);
    }

    public static void signUp(Context context) {
        UrlHandler.open("https://www.douban.com/accounts/register", context);
    }
}
