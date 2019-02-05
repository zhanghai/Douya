/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.media;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.PlaybackParams;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.media.AudioAttributesCompat;
import androidx.core.util.ObjectsCompat;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.PlayerMessage;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.analytics.AnalyticsCollector;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.metadata.MetadataOutput;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.video.VideoListener;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import me.zhanghai.android.douya.util.AudioManagerCompat;
import me.zhanghai.android.douya.util.LogUtils;

@SuppressWarnings("unused")
public class MediaExoPlayer implements ExoPlayer {

    private SimpleExoPlayer mPlayer;
    private AudioAttributesCompat mAudioAttributes;
    private Context mContext;

    private boolean mPlayOnFocusGain;

    private final AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener =
            focusChange -> {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        mPlayer.setVolume(1);
                        if (mPlayOnFocusGain) {
                            mPlayer.setPlayWhenReady(true);
                            mPlayOnFocusGain = false;
                            registerAudioBecomingNoisyReceiver();
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        // 0.2f is the constant in Google samples.
                        mPlayer.setVolume(0.2f);
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        mPlayOnFocusGain |= mPlayer.getPlayWhenReady();
                        unregisterAudioBecomingNoisyReceiver();
                        mPlayer.setPlayWhenReady(false);
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        unregisterAudioBecomingNoisyReceiver();
                        abandonAudioFocus();
                        break;
                }
            };

    private final IntentFilter mAudioBecomingNoisyIntentFilter = new IntentFilter(
            AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private final BroadcastReceiver mAudioBecomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setPlayWhenReady(false);
        }
    };
    private boolean mAudioBecomingNoisyReceiverRegistered;

    public MediaExoPlayer(SimpleExoPlayer player, AudioAttributesCompat audioAttributes,
                          Context context) {
        mPlayer = player;
        mAudioAttributes = audioAttributes;
        mContext = context.getApplicationContext();
    }

    public MediaExoPlayer(AudioAttributesCompat audioAttributes, Context context) {
        this(ExoPlayerFactory.newSimpleInstance(context.getApplicationContext(),
                new DefaultTrackSelector()), audioAttributes, context);
    }

    @Override
    public boolean getPlayWhenReady() {
        return mPlayer.getPlayWhenReady() || mPlayOnFocusGain;
    }

    @Override
    public void setPlayWhenReady(boolean playWhenReady) {
        if (getPlayWhenReady() == playWhenReady) {
            return;
        }
        if (playWhenReady) {
            requestAudioFocus();
        } else {
            abandonAudioFocus();
        }
    }

    @Override
    public void addListener(Player.EventListener listener) {
        mPlayer.addListener(new EventListenerWrapper(listener));
    }

    @Override
    public void removeListener(Player.EventListener listener) {
        mPlayer.removeListener(new EventListenerWrapper(listener));
    }

    @Override
    public void release() {
        unregisterAudioBecomingNoisyReceiver();
        mPlayer.release();
    }

    private void requestAudioFocus() {
        int result = AudioManagerCompat.requestAudioFocus(getAudioManager(),
                AudioManager.AUDIOFOCUS_GAIN, mAudioAttributes, mOnAudioFocusChangeListener);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mPlayOnFocusGain = true;
            mOnAudioFocusChangeListener.onAudioFocusChange(AudioManager.AUDIOFOCUS_GAIN);
        } else {
            LogUtils.w("setPlayWhenReady(true) failed, cannot gain audio focus.");
        }
    }

    private void abandonAudioFocus() {
        mPlayOnFocusGain = false;
        mPlayer.setPlayWhenReady(false);
        AudioManagerCompat.abandonAudioFocus(getAudioManager(), mOnAudioFocusChangeListener);
    }

    private AudioManager getAudioManager() {
        return (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    private void registerAudioBecomingNoisyReceiver() {
        if (mAudioBecomingNoisyReceiverRegistered) {
            return;
        }
        mContext.registerReceiver(mAudioBecomingNoisyReceiver, mAudioBecomingNoisyIntentFilter);
        mAudioBecomingNoisyReceiverRegistered = true;
    }

    private void unregisterAudioBecomingNoisyReceiver() {
        if (!mAudioBecomingNoisyReceiverRegistered) {
            return;
        }
        mContext.unregisterReceiver(mAudioBecomingNoisyReceiver);
        mAudioBecomingNoisyReceiverRegistered = false;
    }

    private class EventListenerWrapper implements Player.EventListener {

        private Player.EventListener mListener;

        private EventListenerWrapper(Player.EventListener listener) {
            mListener = listener;
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            mListener.onPlayerStateChanged(getPlayWhenReady(), playbackState);
        }

        @Override
        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }
            if (object == null || getClass() != object.getClass()) {
                return false;
            }
            EventListenerWrapper that = (EventListenerWrapper) object;
            return ObjectsCompat.equals(mListener, that.mListener);
        }

        @Override
        public int hashCode() {
            return ObjectsCompat.hash(mListener);
        }


        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
            mListener.onTimelineChanged(timeline, manifest, reason);
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups,
                                    TrackSelectionArray trackSelections) {
            mListener.onTracksChanged(trackGroups, trackSelections);
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            mListener.onLoadingChanged(isLoading);
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            mListener.onRepeatModeChanged(repeatMode);
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            mListener.onShuffleModeEnabledChanged(shuffleModeEnabled);
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            mListener.onPlayerError(error);
        }

        @Override
        public void onPositionDiscontinuity(int reason) {
            mListener.onPositionDiscontinuity(reason);
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            mListener.onPlaybackParametersChanged(playbackParameters);
        }

        @Override
        public void onSeekProcessed() {
            mListener.onSeekProcessed();
        }
    }


    @Override
    public Looper getPlaybackLooper() {
        return mPlayer.getPlaybackLooper();
    }

    @Nullable
    @Override
    public ExoPlaybackException getPlaybackError() {
        return mPlayer.getPlaybackError();
    }

    @Override
    public void prepare(MediaSource mediaSource) {
        mPlayer.prepare(mediaSource);
    }

    @Override
    public void prepare(MediaSource mediaSource, boolean resetPosition, boolean resetState) {
        mPlayer.prepare(mediaSource, resetPosition, resetState);
    }

    @Override
    public PlayerMessage createMessage(PlayerMessage.Target target) {
        return mPlayer.createMessage(target);
    }

    @Deprecated
    @Override
    @SuppressWarnings("deprecation")
    public void sendMessages(ExoPlayerMessage... messages) {
        mPlayer.sendMessages(messages);
    }

    @Deprecated
    @Override
    @SuppressWarnings("deprecation")
    public void blockingSendMessages(ExoPlayerMessage... messages) {
        mPlayer.blockingSendMessages(messages);
    }

    @Override
    public void setSeekParameters(@Nullable SeekParameters seekParameters) {
        mPlayer.setSeekParameters(seekParameters);
    }

    @Nullable
    @Override
    public VideoComponent getVideoComponent() {
        return mPlayer.getVideoComponent();
    }

    @Nullable
    @Override
    public TextComponent getTextComponent() {
        return mPlayer.getTextComponent();
    }

    @Override
    public int getPlaybackState() {
        return mPlayer.getPlaybackState();
    }

    @Override
    public void setRepeatMode(int repeatMode) {
        mPlayer.setRepeatMode(repeatMode);
    }

    @Override
    public int getRepeatMode() {
        return mPlayer.getRepeatMode();
    }

    @Override
    public void setShuffleModeEnabled(boolean shuffleModeEnabled) {
        mPlayer.setShuffleModeEnabled(shuffleModeEnabled);
    }

    @Override
    public boolean getShuffleModeEnabled() {
        return mPlayer.getShuffleModeEnabled();
    }

    @Override
    public boolean isLoading() {
        return mPlayer.isLoading();
    }

    @Override
    public void seekToDefaultPosition() {
        mPlayer.seekToDefaultPosition();
    }

    @Override
    public void seekToDefaultPosition(int windowIndex) {
        mPlayer.seekToDefaultPosition(windowIndex);
    }

    @Override
    public void seekTo(long positionMs) {
        mPlayer.seekTo(positionMs);
    }

    @Override
    public void seekTo(int windowIndex, long positionMs) {
        mPlayer.seekTo(windowIndex, positionMs);
    }

    @Override
    public void setPlaybackParameters(@Nullable PlaybackParameters playbackParameters) {
        mPlayer.setPlaybackParameters(playbackParameters);
    }

    @Override
    public PlaybackParameters getPlaybackParameters() {
        return mPlayer.getPlaybackParameters();
    }

    @Override
    public void stop() {
        mPlayer.stop();
    }

    @Override
    public void stop(boolean reset) {
        mPlayer.stop(reset);
    }

    @Override
    public int getRendererCount() {
        return mPlayer.getRendererCount();
    }

    @Override
    public int getRendererType(int index) {
        return mPlayer.getRendererType(index);
    }

    @Override
    public TrackGroupArray getCurrentTrackGroups() {
        return mPlayer.getCurrentTrackGroups();
    }

    @Override
    public TrackSelectionArray getCurrentTrackSelections() {
        return mPlayer.getCurrentTrackSelections();
    }

    @Nullable
    @Override
    public Object getCurrentManifest() {
        return mPlayer.getCurrentManifest();
    }

    @Override
    public Timeline getCurrentTimeline() {
        return mPlayer.getCurrentTimeline();
    }

    @Override
    public int getCurrentPeriodIndex() {
        return mPlayer.getCurrentPeriodIndex();
    }

    @Override
    public int getCurrentWindowIndex() {
        return mPlayer.getCurrentWindowIndex();
    }

    @Override
    public int getNextWindowIndex() {
        return mPlayer.getNextWindowIndex();
    }

    @Override
    public int getPreviousWindowIndex() {
        return mPlayer.getPreviousWindowIndex();
    }

    @Nullable
    @Override
    public Object getCurrentTag() {
        return mPlayer.getCurrentTag();
    }

    @Override
    public long getDuration() {
        return mPlayer.getDuration();
    }

    @Override
    public long getCurrentPosition() {
        return mPlayer.getCurrentPosition();
    }

    @Override
    public long getBufferedPosition() {
        return mPlayer.getBufferedPosition();
    }

    @Override
    public int getBufferedPercentage() {
        return mPlayer.getBufferedPercentage();
    }

    @Override
    public boolean isCurrentWindowDynamic() {
        return mPlayer.isCurrentWindowDynamic();
    }

    @Override
    public boolean isCurrentWindowSeekable() {
        return mPlayer.isCurrentWindowSeekable();
    }

    @Override
    public boolean isPlayingAd() {
        return mPlayer.isPlayingAd();
    }

    @Override
    public int getCurrentAdGroupIndex() {
        return mPlayer.getCurrentAdGroupIndex();
    }

    @Override
    public int getCurrentAdIndexInAdGroup() {
        return mPlayer.getCurrentAdIndexInAdGroup();
    }

    @Override
    public long getContentPosition() {
        return mPlayer.getContentPosition();
    }


    public void setVideoScalingMode(int videoScalingMode) {
        mPlayer.setVideoScalingMode(videoScalingMode);
    }

    public int getVideoScalingMode() {
        return mPlayer.getVideoScalingMode();
    }

    public void clearVideoSurface() {
        mPlayer.clearVideoSurface();
    }

    public void setVideoSurface(Surface surface) {
        mPlayer.setVideoSurface(surface);
    }

    public void clearVideoSurface(Surface surface) {
        mPlayer.clearVideoSurface(surface);
    }

    public void setVideoSurfaceHolder(SurfaceHolder surfaceHolder) {
        mPlayer.setVideoSurfaceHolder(surfaceHolder);
    }

    public void clearVideoSurfaceHolder(SurfaceHolder surfaceHolder) {
        mPlayer.clearVideoSurfaceHolder(surfaceHolder);
    }

    public void setVideoSurfaceView(SurfaceView surfaceView) {
        mPlayer.setVideoSurfaceView(surfaceView);
    }

    public void clearVideoSurfaceView(SurfaceView surfaceView) {
        mPlayer.clearVideoSurfaceView(surfaceView);
    }

    public void setVideoTextureView(TextureView textureView) {
        mPlayer.setVideoTextureView(textureView);
    }

    public void clearVideoTextureView(TextureView textureView) {
        mPlayer.clearVideoTextureView(textureView);
    }

    @Deprecated
    @SuppressWarnings("deprecation")
    public void setAudioStreamType(int streamType) {
        mPlayer.setAudioStreamType(streamType);
    }

    @Deprecated
    @SuppressWarnings("deprecation")
    public int getAudioStreamType() {
        return mPlayer.getAudioStreamType();
    }

    public AnalyticsCollector getAnalyticsCollector() {
        return mPlayer.getAnalyticsCollector();
    }

    public void addAnalyticsListener(AnalyticsListener listener) {
        mPlayer.addAnalyticsListener(listener);
    }

    public void removeAnalyticsListener(AnalyticsListener listener) {
        mPlayer.removeAnalyticsListener(listener);
    }

    public void setAudioAttributes(AudioAttributes audioAttributes) {
        mPlayer.setAudioAttributes(audioAttributes);
    }

    public AudioAttributes getAudioAttributes() {
        return mPlayer.getAudioAttributes();
    }

    public void setVolume(float audioVolume) {
        mPlayer.setVolume(audioVolume);
    }

    public float getVolume() {
        return mPlayer.getVolume();
    }

    @Deprecated
    @SuppressWarnings("deprecation")
    @TargetApi(23)
    public void setPlaybackParams(@Nullable PlaybackParams params) {
        mPlayer.setPlaybackParams(params);
    }

    public Format getVideoFormat() {
        return mPlayer.getVideoFormat();
    }

    public Format getAudioFormat() {
        return mPlayer.getAudioFormat();
    }

    public int getAudioSessionId() {
        return mPlayer.getAudioSessionId();
    }

    public DecoderCounters getVideoDecoderCounters() {
        return mPlayer.getVideoDecoderCounters();
    }

    public DecoderCounters getAudioDecoderCounters() {
        return mPlayer.getAudioDecoderCounters();
    }

    public void addVideoListener(VideoListener listener) {
        mPlayer.addVideoListener(listener);
    }

    public void removeVideoListener(VideoListener listener) {
        mPlayer.removeVideoListener(listener);
    }

    @Deprecated
    @SuppressWarnings("deprecation")
    public void setVideoListener(SimpleExoPlayer.VideoListener listener) {
        mPlayer.setVideoListener(listener);
    }

    @Deprecated
    @SuppressWarnings("deprecation")
    public void clearVideoListener(SimpleExoPlayer.VideoListener listener) {
        mPlayer.clearVideoListener(listener);
    }

    public void addTextOutput(TextOutput listener) {
        mPlayer.addTextOutput(listener);
    }

    public void removeTextOutput(TextOutput listener) {
        mPlayer.removeTextOutput(listener);
    }

    @Deprecated
    @SuppressWarnings("deprecation")
    public void setTextOutput(TextOutput output) {
        mPlayer.setTextOutput(output);
    }

    @Deprecated
    @SuppressWarnings("deprecation")
    public void clearTextOutput(TextOutput output) {
        mPlayer.clearTextOutput(output);
    }

    public void addMetadataOutput(MetadataOutput listener) {
        mPlayer.addMetadataOutput(listener);
    }

    public void removeMetadataOutput(MetadataOutput listener) {
        mPlayer.removeMetadataOutput(listener);
    }

    @Deprecated
    @SuppressWarnings("deprecation")
    public void setMetadataOutput(MetadataOutput output) {
        mPlayer.setMetadataOutput(output);
    }

    @Deprecated
    @SuppressWarnings("deprecation")
    public void clearMetadataOutput(MetadataOutput output) {
        mPlayer.clearMetadataOutput(output);
    }

    @Deprecated
    @SuppressWarnings("deprecation")
    public void setVideoDebugListener(VideoRendererEventListener listener) {
        mPlayer.setVideoDebugListener(listener);
    }

    @Deprecated
    @SuppressWarnings("deprecation")
    public void addVideoDebugListener(VideoRendererEventListener listener) {
        mPlayer.addVideoDebugListener(listener);
    }

    @Deprecated
    @SuppressWarnings("deprecation")
    public void removeVideoDebugListener(VideoRendererEventListener listener) {
        mPlayer.removeVideoDebugListener(listener);
    }

    @Deprecated
    @SuppressWarnings("deprecation")
    public void setAudioDebugListener(AudioRendererEventListener listener) {
        mPlayer.setAudioDebugListener(listener);
    }

    @Deprecated
    @SuppressWarnings("deprecation")
    public void addAudioDebugListener(AudioRendererEventListener listener) {
        mPlayer.addAudioDebugListener(listener);
    }

    @Deprecated
    @SuppressWarnings("deprecation")
    public void removeAudioDebugListener(AudioRendererEventListener listener) {
        mPlayer.removeAudioDebugListener(listener);
    }
}
