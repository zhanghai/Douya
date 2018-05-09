/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.media;

import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator;

public abstract class MediaQueueNavigator extends TimelineQueueNavigator {

    public MediaQueueNavigator(MediaSessionCompat mediaSession) {
        super(mediaSession, Integer.MAX_VALUE);
    }

    @Override
    public long getSupportedQueueNavigatorActions(Player player) {
        if (player == null || player.getCurrentTimeline().isEmpty()) {
            return 0;
        }
        return PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                | PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM;
    }

    @Override
    public void onSkipToPrevious(Player player) {
        if (player.getCurrentTimeline().isEmpty()) {
            return;
        }
        int previousWindowIndex = player.getPreviousWindowIndex();
        if (previousWindowIndex == C.INDEX_UNSET) {
            // Can happen when current window is the first window; In this case we just keep the
            // window and reset the position.
            previousWindowIndex = player.getCurrentWindowIndex();
        }
        player.seekToDefaultPosition(previousWindowIndex);
        player.setPlayWhenReady(true);
    }

    @Override
    public void onSkipToQueueItem(Player player, long id) {
        Timeline timeline = player.getCurrentTimeline();
        if (timeline.isEmpty()) {
            return;
        }
        int windowIndex = (int) id;
        if (windowIndex < 0 || windowIndex >= timeline.getWindowCount()) {
            return;
        }
        player.seekToDefaultPosition(windowIndex);
        player.setPlayWhenReady(true);
    }

    @Override
    public void onSkipToNext(Player player) {
        if (player.getCurrentTimeline().isEmpty()) {
            return;
        }
        int nextWindowIndex = player.getNextWindowIndex();
        if (nextWindowIndex == C.INDEX_UNSET) {
            // Can happen when current window is the last window; In this case we just keep the
            // window and reset the position.
            nextWindowIndex = player.getCurrentWindowIndex();
        }
        player.seekToDefaultPosition(nextWindowIndex);
        player.setPlayWhenReady(true);
    }
}
