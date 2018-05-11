/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.media;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.media.session.PlaybackStateCompat.MediaKeyAction;
import android.view.KeyEvent;

import me.zhanghai.android.douya.functional.compat.Supplier;
import me.zhanghai.android.douya.util.LogUtils;

public class MediaButtonReceiver extends BroadcastReceiver {

    private static Supplier<MediaSessionCompat> sMediaSessionHost;

    public static PendingIntent makePendingIntent(Context context, @MediaKeyAction long action) {
        int keyCode = PlaybackStateCompat.toKeyCode(action);
        if (keyCode == KeyEvent.KEYCODE_UNKNOWN) {
            LogUtils.e("Cannot build a media button pending intent with the given action: " +
                    action);
            return null;
        }
        Intent intent = new Intent(context, MediaButtonReceiver.class)
                .setAction(Intent.ACTION_MEDIA_BUTTON)
                .putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
        return PendingIntent.getBroadcast(context, keyCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void setMediaSessionHost(Supplier<MediaSessionCompat> mediaSessionHost) {
        sMediaSessionHost = mediaSessionHost;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())
                || !intent.hasExtra(Intent.EXTRA_KEY_EVENT)) {
            LogUtils.w("Ignore unsupported intent: " + intent);
            return;
        }
        if (sMediaSessionHost == null) {
            return;
        }
        KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        sMediaSessionHost.get().getController().dispatchMediaButtonEvent(keyEvent);
    }
}
