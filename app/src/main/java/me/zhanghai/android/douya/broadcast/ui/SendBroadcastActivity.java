/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import me.zhanghai.android.douya.util.DoubanUtils;
import me.zhanghai.android.douya.util.FragmentUtils;

public class SendBroadcastActivity extends AppCompatActivity {

    private SendBroadcastFragment mFragment;

    public static Intent makeIntent(Context context) {
        return new Intent(context, SendBroadcastActivity.class);
    }

    public static Intent makeIntent(CharSequence text, Context context) {
        return makeIntent(context)
                .putExtra(Intent.EXTRA_TEXT, text);
    }

    public static Intent makeIntent(CharSequence text, Uri stream, Context context) {
        return makeIntent(text, context)
                .putExtra(Intent.EXTRA_STREAM, stream);
    }

    public static Intent makeTopicIntent(String topic, Context context) {
        CharSequence text = !TextUtils.isEmpty(topic) ? DoubanUtils.makeTopicString(topic) : null;
        return makeIntent(text, context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Calls ensureSubDecor().
        findViewById(android.R.id.content);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            CharSequence text = intent.getCharSequenceExtra(Intent.EXTRA_TEXT);
            Uri stream = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            mFragment = SendBroadcastFragment.newInstance(text, stream);
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
