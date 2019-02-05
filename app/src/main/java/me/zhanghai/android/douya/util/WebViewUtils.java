/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.annotation.RequiresApi;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;

import java.io.File;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.Http;

public class WebViewUtils {

    private WebViewUtils() {}

    public static String getDatabasePath(Context context) {
        File webViewDirectory = context.getDir("webview", Context.MODE_PRIVATE);
        File databasesDirectory = new File(webViewDirectory, "databases");
        if (!databasesDirectory.exists()) {
            // Return valued wasn't checked in {@link ContextImpl#getDir(String, int)}.
            //noinspection ResultOfMethodCallIgnored
            databasesDirectory.mkdir();
        }
        return databasesDirectory.getPath();
    }

    /*
     * @see com.android.webview.chromium.FileChooserParamsAdapter#createFileChooserIntent()
     * @see org.chromium.android_webview.AwContentsClient.FileChooserParamsImpl
     *      #createFileChooserIntent()
     */
    public static Intent createFileChooserIntent(String acceptType) {
        if (acceptType != null) {
            acceptType = acceptType.trim();
        }
        String[] acceptTypes = !TextUtils.isEmpty(acceptType) ? acceptType.split(",") : null;
        return IntentUtils.makePickFile(acceptTypes, false);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static Intent createFileChooserIntent(
            WebChromeClient.FileChooserParams fileChooserParams) {
        boolean allowMultiple = fileChooserParams.getMode()
                == WebChromeClient.FileChooserParams.MODE_OPEN_MULTIPLE;
        String[] acceptTypes = fileChooserParams.getAcceptTypes();
        // Work around for WebView accept types bug.
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
    public static Uri[] parseFileChooserResult(int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null) {
            return null;
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                int itemCount = clipData.getItemCount();
                if (itemCount > 0) {
                    Uri[] uris = new Uri[itemCount];
                    for (int i = 0; i < itemCount; ++i) {
                        uris[i] = clipData.getItemAt(i).getUri();
                    }
                    return uris;
                }
            }
        }
        Uri uri = data.getData();
        if (uri != null) {
            return new Uri[] { uri };
        }
        return null;
    }

    /*
     * @see com.android.browser.DownloadHandler#onDownloadStartNoStream(Activity, String, String,
     *      String, String, String, boolean)
     */
    public static void download(String url, String userAgent, String contentDisposition,
                                String mimeType, Context context) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        if (!TextUtils.isEmpty(userAgent)) {
            request.addRequestHeader(Http.Headers.USER_AGENT, userAgent);
        }
        String cookies = CookieManager.getInstance().getCookie(url);
        request.addRequestHeader(Http.Headers.Cookie, cookies);
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(
                Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
        ToastUtils.show(R.string.webview_downloading, context);
    }
}
