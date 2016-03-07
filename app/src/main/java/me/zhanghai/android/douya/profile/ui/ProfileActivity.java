/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.profile.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.ui.ProfileHeaderLayout;
import me.zhanghai.android.douya.ui.ProfileLayout;
import me.zhanghai.android.douya.util.StatusBarColorUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class ProfileActivity extends AppCompatActivity {

    private static final String KEY_PREFIX = ProfileActivity.class.getName() + '.';

    private static final String EXTRA_BROADCAST_ID = KEY_PREFIX + "user_id_or_uid";

    @BindColor(android.R.color.transparent)
    int mStatusBarColorTransparent;
    private int mStatusBarColorFullscreen;

    @Bind(R.id.scroll)
    ProfileLayout mScrollLayout;
    @Bind(R.id.header)
    ProfileHeaderLayout mHeaderLayout;
    @Bind(R.id.dismiss)
    View mDismissView;

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
        mStatusBarColorFullscreen = ViewUtils.getColorFromAttrRes(R.attr.colorPrimaryDark, 0, this);

        mScrollLayout.setListener(new ProfileLayout.Listener() {
            @Override
            public void onEnterAnimationEnd() {

            }

            @Override
            public void onExitAnimationEnd() {
                finish();
            }
        });
        mScrollLayout.enter();

        StatusBarColorUtils.set(mStatusBarColorTransparent, this);
        mHeaderLayout.setListener(new ProfileHeaderLayout.Listener() {
            @Override
            public void onHeaderReachedTop() {
                StatusBarColorUtils.animateTo(mStatusBarColorFullscreen, ProfileActivity.this);
            }
            @Override
            public void onHeaderLeftTop() {
                StatusBarColorUtils.animateTo(mStatusBarColorTransparent, ProfileActivity.this);
            }
        });

        mDismissView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit();
            }
        });
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    private void exit() {
        mScrollLayout.exit();
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(0, 0);
    }
}
