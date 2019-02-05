/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.profile.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import me.zhanghai.android.douya.network.api.info.apiv2.SimpleUser;
import me.zhanghai.android.douya.network.api.info.apiv2.User;
import me.zhanghai.android.douya.util.FragmentUtils;

public class ProfileActivity extends AppCompatActivity {

    private static final String KEY_PREFIX = ProfileFragment.class.getName() + '.';

    private static final String EXTRA_USER_ID_OR_UID = KEY_PREFIX + "user_id_or_uid";
    private static final String EXTRA_SIMPLE_USER = KEY_PREFIX + "simple_user";
    private static final String EXTRA_USER_INFO = KEY_PREFIX + "user_info";

    private ProfileFragment mFragment;

    public static Intent makeIntent(String userIdOrUid, Context context) {
        return new Intent(context, ProfileActivity.class)
                .putExtra(EXTRA_USER_ID_OR_UID, userIdOrUid);
    }

    public static Intent makeIntent(SimpleUser simpleUser, Context context) {
        return new Intent(context, ProfileActivity.class)
                .putExtra(EXTRA_SIMPLE_USER, simpleUser);
    }

    public static Intent makeIntent(
            me.zhanghai.android.douya.network.api.info.frodo.SimpleUser simpleUser,
            Context context) {
        return makeIntent(SimpleUser.fromFrodo(simpleUser), context);
    }

    public static Intent makeIntent(User user, Context context) {
        return new Intent(context, ProfileActivity.class)
                .putExtra(EXTRA_USER_INFO, user);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(0, 0);

        // Calls ensureSubDecor().
        findViewById(android.R.id.content);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            String userIdOrUid = intent.getStringExtra(EXTRA_USER_ID_OR_UID);
            SimpleUser simpleUser = intent.getParcelableExtra(EXTRA_SIMPLE_USER);
            User user = intent.getParcelableExtra(EXTRA_USER_INFO);
            mFragment = ProfileFragment.newInstance(userIdOrUid, simpleUser, user);
            FragmentUtils.add(mFragment, this, android.R.id.content);
        } else {
            mFragment = FragmentUtils.findById(this, android.R.id.content);
        }
    }

    @Override
    public void onBackPressed() {
        mFragment.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(0, 0);
    }
}
