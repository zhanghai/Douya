/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.media;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import androidx.media.AudioAttributesCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.TextUtils;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ext.mediasession.DefaultPlaybackController;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.douya.functional.Functional;
import me.zhanghai.android.douya.functional.compat.Function;
import me.zhanghai.android.douya.util.UriUtils;

public class MediaPlayback {

    private Function<MediaMetadataCompat, MediaSource> mCreateMediaSource;
    private Runnable mStop;

    private List<MediaMetadataCompat> mMediaMetadatas = new ArrayList<>();

    // We do need the locks.
    // @see https://google.github.io/ExoPlayer/faqs.html#how-do-i-keep-audio-playing-when-my-app-is-backgrounded
    // @see https://github.com/google/ExoPlayer/issues/930#issuecomment-154859256
    private PowerManager.WakeLock mWakeLock;
    private boolean mNeedWifiLock;
    private WifiManager.WifiLock mWifiLock;

    private ExoPlayer mPlayer;
    private MediaSessionCompat mMediaSession;
    private MediaSessionConnector mMediaSessionConnector;

    public MediaPlayback(Function<MediaMetadataCompat, MediaSource> createMediaSource,
                         Runnable stop, Context context) {

        mCreateMediaSource = createMediaSource;
        mStop = stop;

        context = context.getApplicationContext();

        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        String lockTag = getClass().getName();
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, lockTag);
        mWakeLock.setReferenceCounted(false);
        // Our context is already application context.
        @SuppressLint("WifiManagerPotentialLeak")
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mWifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, lockTag);
        mWifiLock.setReferenceCounted(false);

        AudioAttributesCompat audioAttributes = new AudioAttributesCompat.Builder()
                .setUsage(AudioAttributesCompat.USAGE_MEDIA)
                .setContentType(AudioAttributesCompat.CONTENT_TYPE_MUSIC)
                .build();
        mPlayer = new MediaExoPlayer(audioAttributes, context);
        mPlayer.addListener(new Player.DefaultEventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
                updateMediaSessionMetadata();
            }
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                // Must do this before the following if block because it can be recursive.
                holdLocks(playWhenReady);
                // If we don't check for playWhenReady, we end up called recursively from pause().
                if (playWhenReady && playbackState == Player.STATE_ENDED
                        && getRepeatMode() == Player.REPEAT_MODE_OFF
                        && getActiveQueueItemIndex() == getQueueSize() - 1) {
                    pause();
                    skipToQueueItem(0);
                }
            }
            @Override
            public void onPlayerError(ExoPlaybackException error) {
                error.printStackTrace();
            }
            @Override
            public void onPositionDiscontinuity(int reason) {
                updateMediaSessionMetadata();
            }
        });

        mMediaSession = new MediaSessionCompat(context, context.getPackageName());
        mMediaSessionConnector = new MediaSessionConnector(mMediaSession,
                new DefaultPlaybackController() {
                    @Override
                    public void onStop(Player player) {
                        mStop.run();
                    }
                }, false, null);
        mMediaSessionConnector.setQueueNavigator(new MediaQueueNavigator(mMediaSession) {
            @Override
            public MediaDescriptionCompat getMediaDescription(Player player, int windowIndex) {
                return mMediaMetadatas.get(windowIndex).getDescription();
            }
        });
    }

    public ExoPlayer getPlayer() {
        return mPlayer;
    }

    public MediaSessionCompat getMediaSession() {
        return mMediaSession;
    }

    public void setSessionActivity(PendingIntent sessionActivity) {
        mMediaSession.setSessionActivity(sessionActivity);
    }

    public List<MediaMetadataCompat> getMediaMetadatas() {
        return mMediaMetadatas;
    }

    public void setMediaMetadatas(List<MediaMetadataCompat> mediaMetadatas) {
        mMediaMetadatas.clear();
        mMediaMetadatas.addAll(mediaMetadatas);
        mNeedWifiLock = Functional.some(mMediaMetadatas, mediaMetadata -> {
            String uri = mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI);
            return !TextUtils.isEmpty(uri) && UriUtils.isWebScheme(Uri.parse(uri));
        });
        if (mPlayer.getPlaybackState() != Player.STATE_IDLE) {
            updateMediaSessionMetadata();
        }
    }

    @SuppressLint("WakelockTimeout")
    private void holdLocks(boolean hold) {
        if (mWakeLock.isHeld() != hold) {
            if (hold) {
                // We will release the lock once playback is paused/stopped.
                mWakeLock.acquire();
            } else {
                mWakeLock.release();
            }
        }
        boolean holdWifiLock = mNeedWifiLock && hold;
        if (mWifiLock.isHeld() != holdWifiLock) {
            if (holdWifiLock) {
                mWifiLock.acquire();
            } else {
                mWifiLock.release();
            }
        }
    }

    public void start() {
        mPlayer.prepare(createMediaSource());
        mMediaSessionConnector.setPlayer(mPlayer, null);
        updateMediaSessionMetadata();
        mMediaSession.setActive(true);
    }

    private MediaSource createMediaSource() {
        if (mMediaMetadatas.size() == 1) {
            return mCreateMediaSource.apply(mMediaMetadatas.get(0));
        }
        MediaSource[] mediaSources = new MediaSource[mMediaMetadatas.size()];
        for (int i = 0; i < mMediaMetadatas.size(); ++i) {
            MediaMetadataCompat mediaMetadata = mMediaMetadatas.get(i);
            mediaSources[i] = mCreateMediaSource.apply(mediaMetadata);
        }
        return new ConcatenatingMediaSource(mediaSources);
    }

    private void updateMediaSessionMetadata() {
        int index = getActiveQueueItemIndex();
        if (index == C.INDEX_UNSET || index >= mMediaMetadatas.size()) {
            mMediaSession.setMetadata(null);
        }
        MediaMetadataCompat mediaMetadata = mMediaMetadatas.get(index);
        long duration = mPlayer.getDuration();
        if (duration != C.TIME_UNSET
                && mediaMetadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION) != duration) {
            mediaMetadata = new MediaMetadataCompat.Builder(mediaMetadata)
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                    .build();
        }
        mMediaSession.setMetadata(mediaMetadata);
    }

    public void stop() {
        mPlayer.stop(true);
        mMediaSession.setActive(false);
        mMediaSessionConnector.setPlayer(null, null);
    }

    public void release() {
        mPlayer.release();
        mMediaSession.release();
        holdLocks(false);
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
