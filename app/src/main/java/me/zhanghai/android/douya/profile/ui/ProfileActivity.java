/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.profile.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.ui.ProfileScrollLayout;

public class ProfileActivity extends AppCompatActivity {

    private static final String KEY_PREFIX = ProfileActivity.class.getName() + '.';

    private static final String EXTRA_BROADCAST_ID = KEY_PREFIX + "user_id_or_uid";

    @Bind(R.id.scroll)
    ProfileScrollLayout mScrollLayout;

    public static Intent makeIntent(Context context, String userIdOrUid) {
        return new Intent(context, ProfileActivity.class)
                .putExtra(ProfileActivity.EXTRA_BROADCAST_ID, userIdOrUid);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        overridePendingTransition(0, 0);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile_activity);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));

        mScrollLayout.setListener(new ProfileScrollLayout.Listener() {
            @Override
            public void onEnterAnimationEnd() {

            }

            @Override
            public void onExitAnimationEnd() {
                finish();
            }
        });
        mScrollLayout.enter();
    }

    @Override
    public void onBackPressed() {
        mScrollLayout.exit();
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(0, 0);
    }
}
