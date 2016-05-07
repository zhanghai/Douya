/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

public class IntentUtils {

    private static final String ACTION_INSTALL_SHORTCUT =
            "com.android.launcher.action.INSTALL_SHORTCUT";

    private static final String MIME_TYPE_TEXT_PLAIN = "text/plain";
    private static final String MIME_TYPE_IMAGE_ANY = "image/*";
    private static final String MIME_TYPE_ANY = "*/*";

    private IntentUtils() {}

    public static Intent makeInstallShortcut(int iconRes, int nameRes, Class<?> intentClass,
                                             Context context) {
        return new Intent()
                .putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(context.getApplicationContext(),
                        intentClass))
                .putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(nameRes))
                .putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                        Intent.ShortcutIconResource.fromContext(context, iconRes));
    }

    public static Intent makeInstallShortcutWithAction(int iconRes, int nameRes,
                                                       Class<?> intentClass, Context context) {
        return makeInstallShortcut(iconRes, nameRes, intentClass, context)
                .setAction(ACTION_INSTALL_SHORTCUT);
    }

    public static Intent makeLaunchApp(String packageName, Context context) {
        return context.getPackageManager().getLaunchIntentForPackage(packageName);
    }

    public static Intent makePickFile() {
        return new Intent(Intent.ACTION_GET_CONTENT)
                .setType(MIME_TYPE_ANY)
                .addCategory(Intent.CATEGORY_OPENABLE);
        // TODO: addFlags(Intent.GRANT*) for permission?
    }

    // NOTE: Before Build.VERSION_CODES.JELLY_BEAN htmlText will be no-op.
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static Intent makeSendText(CharSequence text, String htmlText) {
        Intent intent = new Intent()
                .setAction(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && htmlText != null) {
            intent.putExtra(Intent.EXTRA_HTML_TEXT, htmlText);
        }
        return intent.setType(MIME_TYPE_TEXT_PLAIN);
    }

    public static Intent makeSendText(CharSequence text) {
        return makeSendText(text, null);
    }

    public static Intent makeSendImage(Uri imageUri, CharSequence text) {
        return new Intent()
                .setAction(Intent.ACTION_SEND)
                // For maximum compatibility.
                .putExtra(Intent.EXTRA_TEXT, text)
                .putExtra(Intent.EXTRA_TITLE, text)
                .putExtra(Intent.EXTRA_SUBJECT, text)
                // HACK: WeChat moments respects this extra only.
                .putExtra("Kdescription", text)
                .putExtra(Intent.EXTRA_STREAM, imageUri)
                .setType(MIME_TYPE_IMAGE_ANY);
    }

    public static Intent makeView(Uri uri) {
        return new Intent(Intent.ACTION_VIEW, uri);
    }

    public static Intent makeViewAppInMarket(String packageName) {
        return makeView(Uri.parse("market://details?id=" + packageName));
    }
}
