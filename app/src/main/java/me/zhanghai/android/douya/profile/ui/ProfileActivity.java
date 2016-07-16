/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.profile.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Keep;
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
import me.zhanghai.android.douya.app.RetainDataFragment;
import me.zhanghai.android.douya.eventbus.BroadcastDeletedEvent;
import me.zhanghai.android.douya.eventbus.BroadcastUpdatedEvent;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.eventbus.UserInfoUpdatedEvent;
import me.zhanghai.android.douya.network.RequestFragment;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiRequests;
import me.zhanghai.android.douya.network.api.info.Broadcast;
import me.zhanghai.android.douya.network.api.info.User;
import me.zhanghai.android.douya.network.api.info.UserInfo;
import me.zhanghai.android.douya.profile.content.UserInfoResource;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.ToastUtils;

public class ProfileActivity extends AppCompatActivity implements UserInfoResource.Listener,
        RequestFragment.Listener {

    private static final int BROADCAST_COUNT_PER_LOAD = 20;

    private static final int REQUEST_CODE_LOAD_BROADCAST_LIST = 0;

    private static final String KEY_PREFIX = ProfileActivity.class.getName() + '.';

    private static final String EXTRA_USER_ID_OR_UID = KEY_PREFIX + "user_id_or_uid";
    private static final String EXTRA_USER = KEY_PREFIX + "user";
    private static final String EXTRA_USER_INFO = KEY_PREFIX + "user_info";

    private static final String RETAIN_DATA_KEY_BROADCAST_LIST = KEY_PREFIX + "broadcast_list";
    private static final String RETAIN_DATA_KEY_LOADING_BROADCAST_LIST = KEY_PREFIX
            + "loading_broadcast_list";

    @BindView(R.id.scroll)
    ProfileLayout mScrollLayout;
    @BindView(R.id.header)
    ProfileHeaderLayout mHeaderLayout;
    @BindView(R.id.dismiss)
    View mDismissView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.broadcasts)
    ProfileBroadcastsLayout mBroadcastsLayout;

    private List<Broadcast> mBroadcastList;

    private UserInfoResource mUserInfoResource;
    private RetainDataFragment mRetainDataFragment;

    private boolean mLoadingBroadcastList;

    public static Intent makeIntent(String userIdOrUid, Context context) {
        return new Intent(context, ProfileActivity.class)
                .putExtra(ProfileActivity.EXTRA_USER_ID_OR_UID, userIdOrUid);
    }

    public static Intent makeIntent(User user, Context context) {
        return new Intent(context, ProfileActivity.class)
                .putExtra(ProfileActivity.EXTRA_USER, user);
    }

    public static Intent makeIntent(UserInfo userInfo, Context context) {
        return new Intent(context, ProfileActivity.class)
                .putExtra(ProfileActivity.EXTRA_USER_INFO, userInfo);
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
        mUserInfoResource = UserInfoResource.attachTo(userIdOrUid, user, userInfo, this);
        mRetainDataFragment = RetainDataFragment.attachTo(this);

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

        if (mUserInfoResource.hasUserInfo()) {
            mHeaderLayout.bindUserInfo(mUserInfoResource.getUserInfo());
        } else if (mUserInfoResource.hasUser()) {
            mHeaderLayout.bindUser(mUserInfoResource.getUser());
        }

        mBroadcastList = mRetainDataFragment.remove(RETAIN_DATA_KEY_BROADCAST_LIST);
        if (mBroadcastList != null) {
            mBroadcastsLayout.bind(mBroadcastList);
        }

        mLoadingBroadcastList = mRetainDataFragment.removeBoolean(
                RETAIN_DATA_KEY_LOADING_BROADCAST_LIST, false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mRetainDataFragment.put(RETAIN_DATA_KEY_BROADCAST_LIST, mBroadcastList);
        mRetainDataFragment.put(RETAIN_DATA_KEY_LOADING_BROADCAST_LIST, mLoadingBroadcastList);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mBroadcastList == null) {
            loadBroadcastList();
        }

        EventBusUtils.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        EventBusUtils.unregister(this);
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
    public void onVolleyResponse(int requestCode, boolean successful, Object result,
                                 VolleyError error, Object requestState) {
        switch (requestCode) {
            case REQUEST_CODE_LOAD_BROADCAST_LIST:
                //noinspection unchecked
                onLoadBroadcastListResponse(successful, (List<Broadcast>) result, error);
                break;
            default:
                LogUtils.w("Unknown request code " + requestCode + ", with successful=" + successful
                        + ", result=" + result + ", error=" + error);
        }
    }

    @Override
    public void onLoadUserInfoStarted(int requestCode) {}

    @Override
    public void onLoadUserInfoFinished(int requestCode) {}

    @Override
    public void onLoadUserInfoError(int requestCode, VolleyError error) {
        LogUtils.e(error.toString());
        ToastUtils.show(ApiError.getErrorString(error, this), this);
    }

    @Override
    public void onUserInfoChanged(int requestCode, UserInfo newUserInfo) {
        mHeaderLayout.bindUserInfo(newUserInfo);
    }

    private void loadBroadcastList() {

        if (mLoadingBroadcastList) {
            return;
        }
        mLoadingBroadcastList = true;
        mBroadcastsLayout.setLoading();

        ApiRequest<List<Broadcast>> request = ApiRequests.newBroadcastListRequest(
                mUserInfoResource.getUserIdOrUid(), null, null, BROADCAST_COUNT_PER_LOAD, this);
        RequestFragment.startRequest(request, null, this, REQUEST_CODE_LOAD_BROADCAST_LIST);
    }

    private void onLoadBroadcastListResponse(boolean successful, List<Broadcast> result,
                                             VolleyError error) {

        if (successful) {
            mBroadcastList = result;
            mBroadcastsLayout.bind(mBroadcastList);
        } else {
            mBroadcastsLayout.setError();
            LogUtils.e(error.toString());
            ToastUtils.show(ApiError.getErrorString(error, this), this);
        }

        mLoadingBroadcastList = false;
    }

    @Keep
    public void onEventMainThread(BroadcastUpdatedEvent event) {
        // TODO
    }

    @Keep
    public void onEventMainThread(BroadcastDeletedEvent event) {
        // TODO
    }
}
