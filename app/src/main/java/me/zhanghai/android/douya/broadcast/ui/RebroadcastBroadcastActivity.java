/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;
import me.zhanghai.android.douya.util.FragmentUtils;

public class RebroadcastBroadcastActivity extends AppCompatActivity {

    private static final String KEY_PREFIX = RebroadcastBroadcastActivity.class.getName() + '.';

    private static final String EXTRA_BROADCAST_ID = KEY_PREFIX + "broadcast_id";
    private static final String EXTRA_BROADCAST = KEY_PREFIX + "broadcast";
    private static final String EXTRA_TEXT = KEY_PREFIX + "text";

    public static Intent makeIntent(long broadcastId, Context context) {
        return new Intent(context, RebroadcastBroadcastActivity.class)
                .putExtra(EXTRA_BROADCAST_ID, broadcastId);
    }

    public static Intent makeIntent(Broadcast broadcast, Context context) {
        return makeIntent(broadcast.id, context)
                .putExtra(EXTRA_BROADCAST, broadcast);
    }

    public static Intent makeIntent(Broadcast broadcast, CharSequence text, Context context) {
        return makeIntent(broadcast, context)
                .putExtra(Intent.EXTRA_TEXT, text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Calls ensureSubDecor().
        findViewById(android.R.id.content);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            long broadcastId = intent.getLongExtra(EXTRA_BROADCAST_ID, -1);
            Broadcast broadcast = intent.getParcelableExtra(EXTRA_BROADCAST);
            CharSequence text = intent.getCharSequenceExtra(EXTRA_TEXT);
            FragmentUtils.add(RebroadcastBroadcastFragment.newInstance(broadcastId, broadcast,
                    text), this, android.R.id.content);
        }
    }
}
