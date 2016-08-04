/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.profile.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.VolleyError;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.customtabshelper.CustomTabsHelperFragment;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.followship.content.FollowUserManager;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.info.apiv2.Broadcast;
import me.zhanghai.android.douya.network.api.info.apiv2.User;
import me.zhanghai.android.douya.network.api.info.apiv2.UserInfo;
import me.zhanghai.android.douya.network.api.info.frodo.Diary;
import me.zhanghai.android.douya.network.api.info.frodo.Item;
import me.zhanghai.android.douya.network.api.info.frodo.Review;
import me.zhanghai.android.douya.network.api.info.frodo.UserItems;
import me.zhanghai.android.douya.profile.content.ProfileResource;
import me.zhanghai.android.douya.ui.ContentStateLayout;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.ToastUtils;

public class ProfileActivity extends AppCompatActivity implements ProfileResource.Listener,
        ProfileHeaderLayout.Listener, ConfirmUnfollowUserDialogFragment.Listener {

    private static final String KEY_PREFIX = ProfileActivity.class.getName() + '.';

    private static final String EXTRA_USER_ID_OR_UID = KEY_PREFIX + "user_id_or_uid";
    private static final String EXTRA_USER = KEY_PREFIX + "user";
    private static final String EXTRA_USER_INFO = KEY_PREFIX + "user_info";

    @BindView(R.id.scroll)
    ProfileLayout mScrollLayout;
    @BindView(R.id.header)
    ProfileHeaderLayout mHeaderLayout;
    @BindView(R.id.dismiss)
    View mDismissView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.contentState)
    ContentStateLayout mContentStateLayout;
    @BindView(R.id.introduction)
    ProfileIntroductionLayout mIntroductionLayout;
    @BindView(R.id.broadcasts)
    ProfileBroadcastsLayout mBroadcastsLayout;
    @BindView(R.id.followship)
    ProfileFollowshipLayout mFollowshipLayout;
    @BindView(R.id.diaries)
    ProfileDiariesLayout mDiariesLayout;
    @BindView(R.id.books)
    ProfileBooksLayout mBooksLayout;
    @BindView(R.id.movies)
    ProfileMoviesLayout mMoviesLayout;
    @BindView(R.id.music)
    ProfileMusicLayout mMusicLayout;
    @BindView(R.id.reviews)
    ProfileReviewsLayout mReviewsLayout;

    private ProfileResource mProfileResource;

    public static Intent makeIntent(String userIdOrUid, Context context) {
        return new Intent(context, ProfileActivity.class)
                .putExtra(EXTRA_USER_ID_OR_UID, userIdOrUid);
    }

    public static Intent makeIntent(User user, Context context) {
        return new Intent(context, ProfileActivity.class)
                .putExtra(EXTRA_USER, user);
    }

    public static Intent makeIntent(UserInfo userInfo, Context context) {
        return new Intent(context, ProfileActivity.class)
                .putExtra(EXTRA_USER_INFO, userInfo);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        overridePendingTransition(0, 0);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile_activity);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String userIdOrUid = intent.getStringExtra(EXTRA_USER_ID_OR_UID);
        User user = intent.getParcelableExtra(EXTRA_USER);
        UserInfo userInfo = intent.getParcelableExtra(EXTRA_USER_INFO);

        CustomTabsHelperFragment.attachTo(this);
        mProfileResource = ProfileResource.attachTo(userIdOrUid, user, userInfo, this);

        mScrollLayout.setListener(new ProfileLayout.Listener() {
            @Override
            public void onEnterAnimationEnd() {}
            @Override
            public void onExitAnimationEnd() {
                finish();
            }
        });
        mScrollLayout.enter();

        mDismissView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit();
            }
        });

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(null);

        if (mProfileResource.hasUserInfo()) {
            mHeaderLayout.bindUserInfo(mProfileResource.getUserInfo());
        } else if (mProfileResource.hasUser()) {
            mHeaderLayout.bindUser(mProfileResource.getUser());
        }

        if (mProfileResource.isLoaded()) {
            mProfileResource.notifyChangedIfLoaded();
        } else {
            mContentStateLayout.setLoading();
        }
    }

    // When moved into fragment, this will be needed.
    @Override
    protected void onDestroy() {
        super.onDestroy();

        mProfileResource.detach();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // TODO: Block or unblock.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send_doumail:
                // TODO
                return true;
            case R.id.action_blacklist:
                // TODO
                return true;
            case R.id.action_report_abuse:
                // TODO
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onLoadError(int requestCode, VolleyError error) {
        LogUtils.e(error.toString());
        mContentStateLayout.setError();
        ToastUtils.show(ApiError.getErrorString(error, this), this);
    }

    @Override
    public void onUserInfoChanged(int requestCode, UserInfo newUserInfo) {
        mHeaderLayout.bindUserInfo(newUserInfo);
    }

    @Override
    public void onUserInfoWriteStarted(int requestCode) {
        mHeaderLayout.bindUserInfo(mProfileResource.getUserInfo());
    }

    @Override
    public void onUserInfoWriteFinished(int requestCode) {
        mHeaderLayout.bindUserInfo(mProfileResource.getUserInfo());
    }

    @Override
    public void onChanged(int requestCode, UserInfo newUserInfo, List<Broadcast> newBroadcastList,
                          List<User> newFollowingList, List<Diary> newDiaryList,
                          List<UserItems> newUserItemList, List<Review> newReviewList) {
        mIntroductionLayout.bind(newUserInfo);
        mBroadcastsLayout.bind(newUserInfo, newBroadcastList);
        mFollowshipLayout.bind(newUserInfo, newFollowingList);
        mDiariesLayout.bind(newUserInfo, newDiaryList);
        mBooksLayout.bind(newUserInfo, newUserItemList);
        mMoviesLayout.bind(newUserInfo, newUserItemList);
        mMusicLayout.bind(newUserInfo, newUserItemList);
        mReviewsLayout.bind(newUserInfo, newReviewList);
        mContentStateLayout.setLoaded(true);
    }

    @Override
    public void onEditProfile(UserInfo userInfo) {

    }

    @Override
    public void onFollowUser(UserInfo userInfo, boolean follow) {
        if (follow) {
            FollowUserManager.getInstance().write(userInfo, true, this);
        } else {
            ConfirmUnfollowUserDialogFragment.show(this);
        }
    }

    @Override
    public void unfollowUser() {
        FollowUserManager.getInstance().write(mProfileResource.getUserInfo(), false, this);
    }
}
