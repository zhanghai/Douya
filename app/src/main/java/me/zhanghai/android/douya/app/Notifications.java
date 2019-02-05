/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.app;

import androidx.core.app.NotificationManagerCompat;

import me.zhanghai.android.douya.R;

public interface Notifications {

    interface Channels {

        interface SEND_BROADCAST {
            String ID = "send_broadcast";
            int NAME_RES = R.string.notification_channel_send_broadcast_name;
            int DESCRIPTION_RES = R.string.notification_channel_send_broadcast_description;
            int IMPORTANCE = NotificationManagerCompat.IMPORTANCE_LOW;
        }

        interface PLAY_MUSIC {
            String ID = "play_music";
            int NAME_RES = R.string.notification_channel_play_music_name;
            int DESCRIPTION_RES = R.string.notification_channel_play_music_description;
            int IMPORTANCE = NotificationManagerCompat.IMPORTANCE_LOW;
        }
    }

    interface Ids {
        int SENDING_BROADCAST = 1;
        int SEND_BROADCAST_FAILED = 2;
        int PLAYING_MUSIC = 3;
    }
}
