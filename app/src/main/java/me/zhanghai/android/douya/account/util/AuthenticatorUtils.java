/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.account.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import me.zhanghai.android.douya.ui.WebViewActivity;

public class AuthenticatorUtils {

    private AuthenticatorUtils() {}

    public static Intent makeSetApiKeyIntent(Context context) {
        // Implicit intent with ACTION_VIEW is not launched by account manager.
        return WebViewActivity.makeIntent(Uri.parse(
                "https://github.com/zhanghai/DouyaApiKey/releases/latest"), context);
    }

    public static Intent makeWebsiteIntent(Context context) {
        // Implicit intent with ACTION_VIEW is not launched by account manager.
        return WebViewActivity.makeIntent(Uri.parse("https://accounts.douban.com"), context);
    }
}
