/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class FileChooserParamsCompat {

    private FileChooserParamsCompat() {}

    /*
     * @see com.android.webview.chromium.FileChooserParamsAdapter#createIntent()
     * @see org.chromium.android_webview.AwContentsClient.FileChooserParamsImpl#createIntent()
     */
    public static Intent createIntent(String acceptType) {
        String mimeType = "*/*";
        if (acceptType != null) {
            acceptType = acceptType.trim();
            if (!acceptType.isEmpty()) {
                mimeType = acceptType.split(";")[0];
            }
        }
        return IntentUtils.makePickFile(mimeType);
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
