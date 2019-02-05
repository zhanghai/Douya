/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.media;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.RatingCompat;
import android.text.TextUtils;
import android.util.TypedValue;

import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;

import java.util.List;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.app.Notifications;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.eventbus.MusicPlayingStateChangedEvent;
import me.zhanghai.android.douya.functional.Functional;
import me.zhanghai.android.douya.glide.GlideApp;
import me.zhanghai.android.douya.item.ui.MusicActivity;
import me.zhanghai.android.douya.network.api.info.frodo.Music;
import me.zhanghai.android.douya.settings.info.Settings;
import me.zhanghai.android.douya.util.CollectionUtils;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.LongCompat;
import me.zhanghai.android.douya.util.SharedPrefsUtils;
import me.zhanghai.android.douya.util.StringCompat;

public class PlayMusicService extends Service
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String KEY_PREFIX = PlayMusicService.class.getName() + '.';

    private static final String EXTRA_MUSIC = KEY_PREFIX + "music";
    private static final String EXTRA_TRACK_INDEX = KEY_PREFIX + "track_index";
    private static final String EXTRA_PLAY_OR_PAUSE = KEY_PREFIX + "play_or_pause";

    private int mMediaDisplayIconMaxSize;
    private int mMediaArtMaxSize;

    private static PlayMusicService sInstance;

    private OkHttpMediaSourceFactory mMediaSourceFactory;
    private MediaPlayback mMediaPlayback;
    private MediaNotification mMediaNotification;

    private Music mMusic;

    public static void start(Music music, Context context) {
        context.startService(makeIntent(music, context));
    }

    public static void start(Music music, int trackIndex, boolean playOrPause, Context context) {
        context.startService(makeIntent(music, trackIndex, playOrPause, context));
    }

    private static Intent makeIntent(Music music, Context context) {
        return new Intent(context, PlayMusicService.class)
                .putExtra(EXTRA_MUSIC, music);
    }

    private static Intent makeIntent(Music music, int trackIndex, boolean playOrPause,
                                     Context context) {
        return makeIntent(music, context)
                .putExtra(EXTRA_TRACK_INDEX, trackIndex)
                .putExtra(EXTRA_PLAY_OR_PAUSE, playOrPause);
    }

    public static PlayMusicService getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

        Resources resources = getResources();
        mMediaDisplayIconMaxSize = resources.getDimensionPixelSize(
                R.dimen.media_display_icon_max_size);
        // TODO: https://issuetracker.google.com/issues/79631811 is fixed now, uncomment when
        // released.
        // This can actually be 1 smaller than the following:
        //mMediaArtMaxSize = resources.getDimensionPixelSize(R.dimen.media_art_max_size);
        // @see MediaSessionCompat
        mMediaArtMaxSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 320,
                resources.getDisplayMetrics());

        mMediaSourceFactory = new OkHttpMediaSourceFactory();
        mMediaPlayback = new MediaPlayback(this::createMediaSourceFromMediaDescription,
                this::stopSelf, this);
        mMediaPlayback.getPlayer().addListener(new Player.DefaultEventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
                EventBusUtils.postAsync(new MusicPlayingStateChangedEvent(PlayMusicService.this));
            }
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                EventBusUtils.postAsync(new MusicPlayingStateChangedEvent(PlayMusicService.this));
            }
            @Override
            public void onPositionDiscontinuity(int reason) {
                EventBusUtils.postAsync(new MusicPlayingStateChangedEvent(PlayMusicService.this));
            }
        });
        MediaButtonReceiver.setMediaSessionHost(() -> mMediaPlayback.getMediaSession());
        mMediaNotification = new MediaNotification(this, mMediaPlayback.getMediaSession(),
                () -> mMediaPlayback.isPlaying(), Notifications.Channels.PLAY_MUSIC.ID,
                Notifications.Channels.PLAY_MUSIC.NAME_RES,
                Notifications.Channels.PLAY_MUSIC.DESCRIPTION_RES,
                Notifications.Channels.PLAY_MUSIC.IMPORTANCE, Notifications.Ids.PLAYING_MUSIC,
                R.drawable.notification_icon, R.color.douya_primary);
        SharedPrefsUtils.getSharedPrefs().registerOnSharedPreferenceChangeListener(this);
    }

    private MediaSource createMediaSourceFromMediaDescription(MediaMetadataCompat mediaMetadata) {
        Uri uri = Uri.parse(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI));
        return mMediaSourceFactory.create(uri);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        SharedPrefsUtils.getSharedPrefs().unregisterOnSharedPreferenceChangeListener(this);
        mMusic = null;
        mMediaNotification.stop();
        MediaButtonReceiver.setMediaSessionHost(null);
        mMediaPlayback.release();

        sInstance = null;

        EventBusUtils.postAsync(new MusicPlayingStateChangedEvent(PlayMusicService.this));
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
        boolean hasTrackIndex = intent.hasExtra(EXTRA_TRACK_INDEX);
        int trackIndex = intent.getIntExtra(EXTRA_TRACK_INDEX, 0);
        boolean playOrPause = intent.getBooleanExtra(EXTRA_PLAY_OR_PAUSE, false);

        boolean musicChanged = music.id != getMusicId();
        mMusic = music;
        if (musicChanged) {
            mMediaPlayback.stop();
            // TODO: Use dedicated session activity.
            PendingIntent sessionActivity = PendingIntent.getActivity(this,
                    LongCompat.hashCode(getMusicId()), MusicActivity.makeIntent(mMusic, this),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            mMediaPlayback.setSessionActivity(sessionActivity);
            List<MediaMetadataCompat> mediaMetadatas = Functional.map(mMusic.tracks,
                    (track, index) -> makeMediaMetadata(mMusic, track, index));
            mMediaPlayback.setMediaMetadatas(mediaMetadatas);
            loadMediaMetadataDisplayIconAndAlbumArt(mMusic);
            mMediaPlayback.start();
        }

        if (!hasTrackIndex) {
            mMediaPlayback.skipToQueueItem(0);
            mMediaPlayback.play();
        } else {
            if (mMediaPlayback.getActiveQueueItemIndex() != trackIndex) {
                mMediaPlayback.skipToQueueItem(trackIndex);
            }
            if (playOrPause) {
                mMediaPlayback.play();
            } else {
                mMediaPlayback.pause();
            }
        }
        mMediaNotification.start();
    }

    private MediaMetadataCompat makeMediaMetadata(Music music, Music.Track track, int index) {
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.title)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, track.title);
        if (!music.artists.isEmpty()) {
            String artists = StringCompat.join(getString(R.string.item_information_delimiter_slash),
                    music.getArtistNames());
            builder
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artists)
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, artists)
                    .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, artists);
        }
        if (track.duration > 0) {
            int duration = track.duration * 1000;
            builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration);
        }
        builder
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, music.title)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, music.title);
        String date = music.getReleaseDate();
        if (!TextUtils.isEmpty(date)) {
            builder.putString(MediaMetadataCompat.METADATA_KEY_DATE, date);
            if (date.length() > 4) {
                try {
                    long year = Long.parseLong(date.substring(0, 4));
                    builder.putLong(MediaMetadataCompat.METADATA_KEY_YEAR, year);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        String genre = CollectionUtils.firstOrNull(music.genres);
        if (!TextUtils.isEmpty(genre)) {
            builder.putString(MediaMetadataCompat.METADATA_KEY_GENRE, genre);
        }
        builder
                .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, index)
                .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, music.tracks.size());
        String albumArtUri = music.cover.getLargeUrl();
        builder
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, albumArtUri)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, albumArtUri);
        if (music.rating != null) {
            float starRating = music.rating.value / music.rating.max * 5;
            starRating = Math.max(0, Math.min(5, starRating));
            RatingCompat rating = RatingCompat.newStarRating(RatingCompat.RATING_5_STARS,
                    starRating);
            builder.putRating(MediaMetadataCompat.METADATA_KEY_RATING, rating);
        }
        String mediaId = music.id + "#" + index;
        builder
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, track.previewUrl);
        return builder.build();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (!TextUtils.equals(key, Settings.SHOW_ALBUM_ART_ON_LOCK_SCREEN.getKey())) {
            return;
        }
        boolean showAlbumArtOnLockScreen = Settings.SHOW_ALBUM_ART_ON_LOCK_SCREEN.getValue();
        if (showAlbumArtOnLockScreen == mediaMetadataHasAlbumArt()) {
            return;
        }
        if (showAlbumArtOnLockScreen) {
            loadMediaMetadataAlbumArt(mMusic);
        } else {
            updateMediaMetadataDisplayIconAndAlbumArt(null, null, true);
        }
    }

    private boolean mediaMetadataHasAlbumArt() {
        return Functional.some(mMediaPlayback.getMediaMetadatas(), mediaMetadata ->
                mediaMetadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART) != null);
    }

    private void loadMediaMetadataDisplayIconAndAlbumArt(Music music) {
        String albumArtUrl = music.cover.getLargeUrl();
        GlideApp.with(this)
                .asBitmap()
                .dontTransform()
                .downsample(DownsampleStrategy.CENTER_INSIDE)
                .override(mMediaDisplayIconMaxSize)
                .load(albumArtUrl)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap displayIcon,
                                                Transition<? super Bitmap> transition) {
                        if (music.id != getMusicId()) {
                            return;
                        }
                        updateMediaMetadataDisplayIconAndAlbumArt(displayIcon, null, false);
                        if (Settings.SHOW_ALBUM_ART_ON_LOCK_SCREEN.getValue()) {
                            loadMediaMetadataAlbumArt(music);
                        }
                    }
                });
    }

    private void loadMediaMetadataAlbumArt(Music music) {
        String albumArtUrl = music.cover.getLargeUrl();
        GlideApp.with(PlayMusicService.this)
                .asBitmap()
                .dontTransform()
                .downsample(DownsampleStrategy.CENTER_INSIDE)
                .override(mMediaArtMaxSize)
                .load(albumArtUrl)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(
                            Bitmap albumArt,
                            Transition<? super Bitmap> transition) {
                        if (music.id != getMusicId()) {
                            return;
                        }
                        if (!Settings.SHOW_ALBUM_ART_ON_LOCK_SCREEN.getValue()) {
                            return;
                        }
                        updateMediaMetadataDisplayIconAndAlbumArt(null, albumArt, false);
                    }
                });
    }

    private void updateMediaMetadataDisplayIconAndAlbumArt(Bitmap displayIcon, Bitmap albumArt,
                                                           boolean removeAlbumArt) {
        List<MediaMetadataCompat> mediaMetadatas = mMediaPlayback.getMediaMetadatas();
        mediaMetadatas = Functional.map(mediaMetadatas, mediaMetadata -> {
            MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder(mediaMetadata);
            if (displayIcon != null) {
                builder.putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, displayIcon);
            }
            if (albumArt != null) {
                builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt);
            } else if (removeAlbumArt) {
                builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, null);
            }
            return builder.build();
        });
        mMediaPlayback.setMediaMetadatas(mediaMetadatas);
    }

    public long getMusicId() {
        return mMusic != null ? mMusic.id : 0;
    }

    public int getActiveTrackIndex() {
        return mMediaPlayback.getActiveQueueItemIndex();
    }

    public boolean isPlaying() {
        return mMediaPlayback.isPlaying();
    }
}
