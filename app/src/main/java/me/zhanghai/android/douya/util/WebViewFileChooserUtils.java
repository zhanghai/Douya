/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.webkit.WebChromeClient;

public class WebViewFileChooserUtils {

    private WebViewFileChooserUtils() {}

    /*
     * @see com.android.webview.chromium.FileChooserParamsAdapter#createIntent()
     * @see org.chromium.android_webview.AwContentsClient.FileChooserParamsImpl#createIntent()
     */
    public static Intent createIntent(String acceptType) {
        if (acceptType != null) {
            acceptType = acceptType.trim();
        }
        String[] acceptTypes = !TextUtils.isEmpty(acceptType) ? acceptType.split(",") : null;
        return IntentUtils.makePickFile(acceptTypes, false);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static Intent createIntent(WebChromeClient.FileChooserParams fileChooserParams) {
        boolean allowMultiple = fileChooserParams.getMode()
                == WebChromeClient.FileChooserParams.MODE_OPEN_MULTIPLE;
        String[] acceptTypes = fileChooserParams.getAcceptTypes();
        // Fix for WebView accept types bug.
        if (acceptTypes != null && acceptTypes.length == 1 && acceptTypes[0].indexOf(',') != -1) {
            acceptTypes = acceptTypes[0].split(",");
        }
        return IntentUtils.makePickFile(acceptTypes, allowMultiple);
    }

    /*
     * @see com.android.webview.chromium.FileChooserParamsAdapter#parseFileChooserResult(int,
     *      Intent)
     * @see org.chromium.android_webview.AwContentsClient.#parseFileChooserResult(int, Intent)
     */
    public static Uri parseResult(int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null) {
            return null;
        }
        return data.getData();
    }
}
