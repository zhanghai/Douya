/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.media;

import android.content.Context;
import android.support.v4.media.AudioAttributesCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.douya.util.FunctionCompat;

public class MediaPlayback {

    private FunctionCompat.Function<MediaDescriptionCompat, MediaSource> mCreateMediaSource;

    private List<MediaDescriptionCompat> mMediaDescriptions = new ArrayList<>();

    private ExoPlayer mPlayer;
    private MediaSessionCompat mMediaSession;
    private MediaSessionConnector mMediaSessionConnector;

    public MediaPlayback(
            FunctionCompat.Function<MediaDescriptionCompat, MediaSource> createMediaSource,
            Context context) {

        mCreateMediaSource = createMediaSource;

        context = context.getApplicationContext();
        AudioAttributesCompat audioAttributes = new AudioAttributesCompat.Builder()
                .setUsage(AudioAttributesCompat.USAGE_MEDIA)
                .setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
                .build();
        mPlayer = new MediaExoPlayer(audioAttributes, context);
        mPlayer.addListener(new Player.DefaultEventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_ENDED && getRepeatMode() == Player.REPEAT_MODE_OFF
                        && getActiveQueueItemIndex() == getQueueSize() - 1) {
                    pause();
                    skipToQueueItem(0);
                }
            }
        });

        mMediaSession = new MediaSessionCompat(context, context.getPackageName());
        mMediaSessionConnector = new MediaSessionConnector(mMediaSession);
        mMediaSessionConnector.setQueueNavigator(new MediaQueueNavigator(mMediaSession) {
            @Override
            public MediaDescriptionCompat getMediaDescription(Player player, int windowIndex) {
                return mMediaDescriptions.get(windowIndex);
            }
        });
    }

    public ExoPlayer getPlayer() {
        return mPlayer;
    }

    public MediaSessionCompat getMediaSession() {
        return mMediaSession;
    }

    public void setMediaDescriptions(List<MediaDescriptionCompat> mediaDescriptions) {
        mMediaDescriptions.clear();
        mMediaDescriptions.addAll(mediaDescriptions);
    }

    public void start() {
        mPlayer.prepare(createMediaSource());
        mMediaSessionConnector.setPlayer(mPlayer, null);
        mMediaSession.setActive(true);
    }

    private MediaSource createMediaSource() {
        if (mMediaDescriptions.size() == 1) {
            return mCreateMediaSource.apply(mMediaDescriptions.get(0));
        }
        MediaSource[] mediaSources = new MediaSource[mMediaDescriptions.size()];
        for (int i = 0; i < mMediaDescriptions.size(); i++) {
            MediaDescriptionCompat mediaDescription = mMediaDescriptions.get(i);
            mediaSources[i] = mCreateMediaSource.apply(mediaDescription);
        }
        return new ConcatenatingMediaSource(mediaSources);
    }

    public void stop() {
        mPlayer.stop(true);
        mMediaSession.setActive(false);
        mMediaSessionConnector.setPlayer(null, null);
    }

    public void release() {
        mPlayer.release();
        mMediaSession.release();
    }

    public boolean isPlaying() {
        return mPlayer.getPlayWhenReady();
    }

    public void play() {
        mPlayer.setPlayWhenReady(true);
    }

    public void pause() {
        mPlayer.setPlayWhenReady(false);
    }

    public int getQueueSize() {
        return mPlayer.getCurrentTimeline().getWindowCount();
    }

    public int getActiveQueueItemIndex() {
        return mPlayer.getCurrentWindowIndex();
    }

    public void skipToQueueItem(int index) {
        mPlayer.seekToDefaultPosition(index);
    }

    @Player.RepeatMode
    public int getRepeatMode() {
        return mPlayer.getRepeatMode();
    }

    public void setRepeatMode(@Player.RepeatMode int repeatMode) {
        mPlayer.setRepeatMode(repeatMode);
    }

    public boolean getShuffleModeEnabled() {
        return mPlayer.getShuffleModeEnabled();
    }

    public void setShuffleModeEnabled(boolean enabled) {
        mPlayer.setShuffleModeEnabled(enabled);
    }
}
