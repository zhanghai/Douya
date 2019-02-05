/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.douya.ui.FragmentFinishable;
import me.zhanghai.android.douya.util.DoubanUtils;
import me.zhanghai.android.douya.util.FragmentUtils;
import me.zhanghai.android.douya.util.ObjectUtils;
import me.zhanghai.android.douya.util.UrlUtils;

public class SendBroadcastActivity extends AppCompatActivity implements FragmentFinishable {

    private static final String KEY_PREFIX = SendBroadcastActivity.class.getName() + '.';

    private static final String EXTRA_TEXT = KEY_PREFIX + "text";
    private static final String EXTRA_IMAGE_URIS = KEY_PREFIX + "image_uris";
    private static final String EXTRA_LINK_INFO = KEY_PREFIX + "link_info";

    private SendBroadcastFragment mFragment;

    private boolean mShouldFinish;

    public static Intent makeIntent(Context context) {
        return new Intent(context, SendBroadcastActivity.class);
    }

    public static Intent makeIntent(String text, Context context) {
        return makeIntent(context)
                .putExtra(EXTRA_TEXT, text);
    }

    public static Intent makeIntent(String text, List<Uri> imageUris,
                                    SendBroadcastFragment.LinkInfo linkInfo, Context context) {
        return makeIntent(text, context)
                .putExtra(EXTRA_IMAGE_URIS, new ArrayList<>(imageUris))
                .putExtra(EXTRA_LINK_INFO, linkInfo);
    }

    public static Intent makeTopicIntent(String topic, Context context) {
        String text = !TextUtils.isEmpty(topic) ? DoubanUtils.makeTopicBroadcastText(topic) : null;
        return makeIntent(text, context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Calls ensureSubDecor().
        findViewById(android.R.id.content);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            String text;
            boolean allowUrlFromText = true;
            if (intent.hasExtra(EXTRA_TEXT)) {
                text = intent.getStringExtra(EXTRA_TEXT);
                allowUrlFromText = false;
            } else if (intent.hasExtra(Intent.EXTRA_TEXT)) {
                ArrayList<CharSequence> textList = intent.getCharSequenceArrayListExtra(
                        Intent.EXTRA_TEXT);
                if (textList != null) {
                    text = TextUtils.join("\n", textList);
                } else {
                    text = ObjectUtils.toString(intent.getCharSequenceExtra(Intent.EXTRA_TEXT));
                }
            } else {
                text = null;
            }
            ArrayList<Uri> imageUris;
            if (intent.hasExtra(EXTRA_IMAGE_URIS)) {
                imageUris = intent.getParcelableArrayListExtra(EXTRA_IMAGE_URIS);
            } else if (intent.hasExtra(Intent.EXTRA_STREAM)) {
                imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                if (imageUris == null) {
                    imageUris = new ArrayList<>();
                    Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                    if (imageUri != null) {
                        imageUris.add(imageUri);
                    }
                }
            } else {
                imageUris = new ArrayList<>();
            }
            SendBroadcastFragment.LinkInfo linkInfo;
            if (intent.hasExtra(EXTRA_LINK_INFO)) {
                linkInfo = intent.getParcelableExtra(EXTRA_LINK_INFO);
            } else if (allowUrlFromText && UrlUtils.isValidUrl(text)) {
                linkInfo = new SendBroadcastFragment.LinkInfo(text);
                text = null;
            } else {
                linkInfo = null;
            }
            mFragment = SendBroadcastFragment.newInstance(text, imageUris, linkInfo);
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
