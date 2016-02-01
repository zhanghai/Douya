/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.settings.info.Settings;

public class ClipboardUtils {

    private static final int TOAST_COPIED_TEXT_MAX_LENGTH = 40;

    private ClipboardUtils() {}

    private static ClipboardManager getClipboardManager(Context context) {
        return (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    public static void copyText(CharSequence label, CharSequence text, Context context) {
        ClipData clipData = ClipData.newPlainText(label, text);
        getClipboardManager(context).setPrimaryClip(clipData);
        showToast(text, context);
    }

    public static void copyRawUri(CharSequence label, Uri uri, Context context) {
        if (Settings.ALWAYS_COPY_TO_CLIPBOARD_AS_TEXT.getValue(context)) {
            copyText(label, uri.toString(), context);
        } else {
            copyRawUriInt(label, uri, context);
        }
    }

    private static void copyRawUriInt(CharSequence label, Uri uri, Context context) {
        ClipData clipData = ClipData.newRawUri(label, uri);
        getClipboardManager(context).setPrimaryClip(clipData);
        showToast(uri.toString(), context);
    }

    public static void copyUrl(CharSequence label, String url, Context context) {
        if (Settings.ALWAYS_COPY_TO_CLIPBOARD_AS_TEXT.getValue(context)) {
            copyText(label, url, context);
        } else {
            copyUrlInt(label, url, context);
        }
    }

    private static void copyUrlInt(CharSequence label, String url, Context context) {
        copyRawUri(label, Uri.parse(url), context);
    }

    public static void copyUri(CharSequence label, Uri uri, Context context) {
        if (Settings.ALWAYS_COPY_TO_CLIPBOARD_AS_TEXT.getValue(context)) {
            copyText(label, uri.toString(), context);
        } else {
            copyUriInt(label, uri, context);
        }
    }

    public static void copyUriInt(CharSequence label, Uri uri, Context context) {
        ClipData clipData = ClipData.newUri(context.getContentResolver(), label, uri);
        getClipboardManager(context).setPrimaryClip(clipData);
        showToast(uri.toString(), context);
    }

    private static void showToast(CharSequence copiedText, Context context) {
        boolean ellipsized = false;
        if (copiedText.length() > TOAST_COPIED_TEXT_MAX_LENGTH) {
            copiedText = copiedText.subSequence(0, TOAST_COPIED_TEXT_MAX_LENGTH);
            ellipsized = true;
        }
        int indexOfFirstNewline = TextUtils.indexOf(copiedText, '\n');
        if (indexOfFirstNewline != -1) {
            int indexOfSecondNewline = TextUtils.indexOf(copiedText, '\n', indexOfFirstNewline + 1);
            if (indexOfSecondNewline != -1) {
                copiedText = copiedText.subSequence(0, indexOfSecondNewline);
                ellipsized = true;
            }
        }
        if (ellipsized) {
            copiedText = copiedText.toString() + '\u2026';
        }
        ToastUtils.show(context.getString(R.string.copied_to_clipboard_format, copiedText),
                context);
    }
}
