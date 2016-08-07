/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.zhanghai.android.douya.network.api.info.apiv2.User;
import me.zhanghai.android.douya.util.FragmentUtils;
import me.zhanghai.android.douya.util.TransitionUtils;

public class BroadcastListActivity extends AppCompatActivity {

    private static final String KEY_PREFIX = BroadcastListActivity.class.getName() + '.';

    private static final String EXTRA_USER_ID_OR_UID = KEY_PREFIX + "user_id_or_uid";
    private static final String EXTRA_USER = KEY_PREFIX + "user";
    private static final String EXTRA_TOPIC = KEY_PREFIX + "topic";

    public static Intent makeIntent(String userIdOrUid, Context context) {
        return new Intent(context, BroadcastListActivity.class)
                .putExtra(EXTRA_USER_ID_OR_UID, userIdOrUid);
    }

    public static Intent makeIntent(User user, Context context) {
        return new Intent(context, BroadcastListActivity.class)
                .putExtra(EXTRA_USER, user);
    }

    public static Intent makeTopicIntent(String topic, Context context) {
        return new Intent(context, BroadcastListActivity.class)
                .putExtra(EXTRA_TOPIC, topic);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        TransitionUtils.setupTransitionBeforeDecorate(this);

        super.onCreate(savedInstanceState);

        // Calls ensureSubDecor().
        findViewById(android.R.id.content);

        TransitionUtils.postponeTransition(this);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            String userIdOrUid = intent.getStringExtra(EXTRA_USER_ID_OR_UID);
            User user = intent.getParcelableExtra(EXTRA_USER);
            String topic = intent.getStringExtra(EXTRA_TOPIC);
            FragmentUtils.add(BroadcastListActivityFragment.newInstance(userIdOrUid, user, topic),
                    this, android.R.id.content);
        }
    }
}
