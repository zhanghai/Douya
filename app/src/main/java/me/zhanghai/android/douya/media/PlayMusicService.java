/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.media;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaDescriptionCompat;

import com.google.android.exoplayer2.source.MediaSource;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.app.Notifications;
import me.zhanghai.android.douya.item.ui.MusicActivity;
import me.zhanghai.android.douya.network.api.info.frodo.Music;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.ObjectUtils;
import me.zhanghai.android.douya.util.StringCompat;

public class PlayMusicService extends Service {

    private static final String KEY_PREFIX = PlayMusicService.class.getName() + '.';

    private static final String EXTRA_MUSIC = KEY_PREFIX + "music";
    private static final String EXTRA_TRACK_INDEX = KEY_PREFIX + "track_index";

    private OkHttpMediaSourceFactory mMediaSourceFactory;
    private MediaPlayback mMediaPlayback;
    private MediaNotification mMediaNotification;

    private long mMusicId;

    private boolean mStopped;

    public static void start(Music music, int trackIndex, Context context) {
        Intent intent = new Intent(context, PlayMusicService.class)
                .putExtra(EXTRA_MUSIC, music)
                .putExtra(EXTRA_TRACK_INDEX, trackIndex);
        context.startService(intent);
    }

    public static void start(Music music, Context context) {
        start(music, 0, context);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mMediaSourceFactory = new OkHttpMediaSourceFactory();
        mMediaPlayback = new MediaPlayback(this::createMediaSourceFromMediaDescription, this::stop,
                this);
        MediaButtonReceiver.setMediaSessionHost(() -> mMediaPlayback.getMediaSession());
        mMediaNotification = new MediaNotification(this, mMediaPlayback.getMediaSession(),
                () -> mMediaPlayback.isPlaying(), Notifications.Channels.PLAY_MUSIC.ID,
                Notifications.Channels.PLAY_MUSIC.NAME_RES,
                Notifications.Channels.PLAY_MUSIC.DESCRIPTION_RES,
                Notifications.Channels.PLAY_MUSIC.IMPORTANCE, Notifications.Ids.PLAYING_MUSIC,
                R.drawable.notification_icon, R.color.douya_primary);
    }

    private MediaSource createMediaSourceFromMediaDescription(
            MediaDescriptionCompat mediaDescription) {
        return mMediaSourceFactory.create(mediaDescription.getMediaUri());
    }

    private void stop() {
        performStop();
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Just in case.
        performStop();
    }

    private void performStop() {
        if (mStopped) {
            return;
        }
        mMediaNotification.stop();
        MediaButtonReceiver.setMediaSessionHost(null);
        mMediaPlayback.release();
        mStopped = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            LogUtils.e("Intent is null in onStartCommand()");
            return START_NOT_STICKY;
        }
        onHandleIntent(intent);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void onHandleIntent(Intent intent) {

        Music music = intent.getParcelableExtra(EXTRA_MUSIC);
        int trackIndex = intent.getIntExtra(EXTRA_TRACK_INDEX, 0);

        // TODO: Wake lock, wifi lock.
        // TODO: Better Metadata.
        // TODO: Become noisy listener

        boolean musicChanged = music.id != mMusicId;
        mMusicId = music.id;
        if (musicChanged) {
            mMediaPlayback.stop();
            // TODO: Use dedicated session activity.
            PendingIntent sessionActivity = PendingIntent.getActivity(this, 0,
                    MusicActivity.makeIntent(music, this), PendingIntent.FLAG_UPDATE_CURRENT);
            mMediaPlayback.setSessionActivity(sessionActivity);
            List<MediaDescriptionCompat> mediaDescriptions = new ArrayList<>();
            String artists = StringCompat.join(getString(R.string.item_information_delimiter_slash),
                    music.getArtistNames());
            for (int i = 0; i < music.tracks.size(); ++i) {
                Music.Track track = music.tracks.get(i);
                mediaDescriptions.add(new MediaDescriptionCompat.Builder()
                        .setMediaId(makeMediaId(music, i))
                        .setTitle(track.title)
                        .setSubtitle(artists)
                        .setDescription(music.title)
                        // TODO
                        //.setIconBitmap()
                        .setIconUri(Uri.parse(ObjectUtils.firstNonNull(track.coverUrl,
                                music.cover.getLargeUrl())))
                        .setMediaUri(Uri.parse(track.previewUrl))
                        .build());
            }
            mMediaPlayback.setMediaDescriptions(mediaDescriptions);
            mMediaPlayback.start();
        }

        mMediaPlayback.skipToQueueItem(trackIndex);
        mMediaPlayback.play();
        mMediaNotification.start();
    }

    private String makeMediaId(Music music, int index) {
        return music.id + "#" + index;
    }
}
