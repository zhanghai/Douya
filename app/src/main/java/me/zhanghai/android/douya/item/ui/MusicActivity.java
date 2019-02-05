/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import me.zhanghai.android.douya.network.api.info.frodo.Music;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleMusic;
import me.zhanghai.android.douya.util.FragmentUtils;

public class MusicActivity extends AppCompatActivity {

    private static final String KEY_PREFIX = MusicActivity.class.getName() + '.';

    private static final String EXTRA_MUSIC_ID = KEY_PREFIX + "music_id";
    private static final String EXTRA_SIMPLE_MUSIC = KEY_PREFIX + "simple_music";
    private static final String EXTRA_MUSIC = KEY_PREFIX + "music";

    public static Intent makeIntent(long musicId, Context context) {
        return new Intent(context, MusicActivity.class)
                .putExtra(EXTRA_MUSIC_ID, musicId);
    }

    public static Intent makeIntent(SimpleMusic simpleMusic, Context context) {
        return makeIntent(simpleMusic.id, context)
                .putExtra(EXTRA_SIMPLE_MUSIC, simpleMusic);
    }

    public static Intent makeIntent(Music music, Context context) {
        return makeIntent((SimpleMusic) music, context)
                .putExtra(EXTRA_MUSIC, music);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Calls ensureSubDecor().
        findViewById(android.R.id.content);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            long musicId = intent.getLongExtra(EXTRA_MUSIC_ID, -1);
            SimpleMusic simpleMusic = intent.getParcelableExtra(EXTRA_SIMPLE_MUSIC);
            Music music = intent.getParcelableExtra(EXTRA_MUSIC);
            FragmentUtils.add(MusicFragment.newInstance(musicId, simpleMusic, music), this,
                    android.R.id.content);
        }
    }
}
