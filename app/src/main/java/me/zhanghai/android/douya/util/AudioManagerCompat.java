/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import androidx.media.AudioAttributesCompat;

import java.util.WeakHashMap;

public class AudioManagerCompat {

    private static final WeakHashMap<AudioManager.OnAudioFocusChangeListener, Object>
            sListenerRequestMap = new WeakHashMap<>();

    private AudioManagerCompat() {}

    public static int requestAudioFocus(AudioManager audioManager,
                                        int focusGain, AudioAttributesCompat attributes,
                                        AudioManager.OnAudioFocusChangeListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioFocusRequest request = new AudioFocusRequest.Builder(focusGain)
                    .setAudioAttributes((AudioAttributes) attributes.unwrap())
                    .setOnAudioFocusChangeListener(listener)
                    .build();
            sListenerRequestMap.put(listener, request);
            return audioManager.requestAudioFocus(request);
        } else {
            //noinspection deprecation
            return audioManager.requestAudioFocus(listener, attributes.getLegacyStreamType(),
                    focusGain);
        }
    }

    public static int abandonAudioFocus(AudioManager audioManager,
                                        AudioManager.OnAudioFocusChangeListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioFocusRequest request = (AudioFocusRequest) sListenerRequestMap.remove(listener);
            if (listener != null && request == null) {
                // The same behavior as abandoning a never requested or already abandoned request.
                return AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
            }
            return audioManager.abandonAudioFocusRequest(request);
        } else {
            //noinspection deprecation
            return audioManager.abandonAudioFocus(listener);
        }
    }
}
