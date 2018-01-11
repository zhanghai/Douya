/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.zhanghai.android.douya.util.FragmentUtils;

public class SendCommentActivity extends AppCompatActivity {

    private static final String KEY_PREFIX = SendCommentActivity.class.getName() + '.';

    private static final String EXTRA_BROADCAST_ID = KEY_PREFIX + "broadcast_id";
    private static final String EXTRA_TEXT = KEY_PREFIX + "text";

    private SendCommentFragment mFragment;

    public static Intent makeIntent(long broadcastId, Context context) {
        return new Intent(context, SendCommentActivity.class)
                .putExtra(EXTRA_BROADCAST_ID, broadcastId);
    }

    public static Intent makeIntent(long broadcastId, CharSequence text, Context context) {
        return makeIntent(broadcastId, context)
                .putExtra(EXTRA_TEXT, text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Calls ensureSubDecor().
        findViewById(android.R.id.content);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            long broadcastId = intent.getLongExtra(EXTRA_BROADCAST_ID, -1);
            CharSequence text = intent.getCharSequenceExtra(EXTRA_TEXT);
            mFragment = SendCommentFragment.newInstance(broadcastId, text);
            FragmentUtils.add(mFragment, this, android.R.id.content);
        } else {
            mFragment = FragmentUtils.findById(this, android.R.id.content);
        }
    }

    @Override
    public void onBackPressed() {
        mFragment.onFinish();
    }
}
