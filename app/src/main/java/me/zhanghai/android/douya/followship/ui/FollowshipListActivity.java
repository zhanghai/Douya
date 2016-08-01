/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.followship.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.zhanghai.android.douya.util.FragmentUtils;

public abstract class FollowshipListActivity extends AppCompatActivity {

    private static final String KEY_PREFIX = FollowshipListActivity.class.getName() + '.';

    protected static final String EXTRA_USER_ID_OR_UID = KEY_PREFIX + "user_id_or_uid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Calls ensureSubDecor().
        findViewById(android.R.id.content);

        if (savedInstanceState == null) {
            String userIdOrUid = getIntent().getStringExtra(EXTRA_USER_ID_OR_UID);
            FragmentUtils.add(onCreateActivityFragment(userIdOrUid), this, android.R.id.content);
        }
    }

    abstract protected FollowshipListActivityFragment onCreateActivityFragment(String userIdOrUid);
}
