/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.profile.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.zhanghai.android.douya.R;

public class ProfileActivity extends AppCompatActivity {

    private static final String KEY_PREFIX = ProfileActivity.class.getName() + '.';

    private static final String EXTRA_BROADCAST_ID = KEY_PREFIX + "user_id_or_uid";

    public static Intent makeIntent(Context context, String userIdOrUid) {
        return new Intent(context, ProfileActivity.class)
                .putExtra(ProfileActivity.EXTRA_BROADCAST_ID, userIdOrUid);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        overridePendingTransition(0, 0);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile_activity);
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(0, 0);
    }
}
