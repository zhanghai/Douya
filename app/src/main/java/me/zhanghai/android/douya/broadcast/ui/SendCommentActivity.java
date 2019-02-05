/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import me.zhanghai.android.douya.ui.FragmentFinishable;
import me.zhanghai.android.douya.util.FragmentUtils;

public class SendCommentActivity extends AppCompatActivity implements FragmentFinishable {

    private static final String KEY_PREFIX = SendCommentActivity.class.getName() + '.';

    private static final String EXTRA_BROADCAST_ID = KEY_PREFIX + "broadcast_id";
    private static final String EXTRA_TEXT = KEY_PREFIX + "text";

    private SendCommentFragment mFragment;

    private boolean mShouldFinish;

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
    public void finish() {
        if (!mShouldFinish) {
            mFragment.onFinish();
            return;
        }
        super.finish();
    }

    @Override
    public void finishAfterTransition() {
        if (!mShouldFinish) {
            mFragment.onFinish();
            return;
        }
        super.finishAfterTransition();
    }

    @Override
    public void finishFromFragment() {
        mShouldFinish = true;
        super.finish();
    }

    @Override
    public void finishAfterTransitionFromFragment() {
        mShouldFinish = true;
        super.supportFinishAfterTransition();
    }
}
