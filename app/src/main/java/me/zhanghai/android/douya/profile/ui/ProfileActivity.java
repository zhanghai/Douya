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
import android.widget.ProgressBar;

import com.android.volley.VolleyError;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import me.zhanghai.android.customtabshelper.CustomTabsHelperFragment;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.app.RetainDataFragment;
import me.zhanghai.android.douya.eventbus.UserInfoUpdatedEvent;
import me.zhanghai.android.douya.network.RequestFragment;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiRequests;
import me.zhanghai.android.douya.network.api.info.User;
import me.zhanghai.android.douya.network.api.info.UserInfo;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.ToastUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class ProfileActivity extends AppCompatActivity implements RequestFragment.Listener {

    private static final int REQUEST_CODE_LOAD_USER_INFO = 0;

    private static final String KEY_PREFIX = ProfileActivity.class.getName() + '.';

    public static final String EXTRA_USER_ID_OR_UID = KEY_PREFIX + "user_id_or_uid";
    public static final String EXTRA_USER = KEY_PREFIX + "user";
    public static final String EXTRA_USER_INFO = KEY_PREFIX + "user_info";

    private static final String RETAIN_DATA_KEY_USER_INFO = KEY_PREFIX + "user_info";

    @Bind(R.id.scroll)
    ProfileLayout mScrollLayout;
    @Bind(R.id.header)
    ProfileHeaderLayout mHeaderLayout;
    @Bind(R.id.dismiss)
    View mDismissView;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.progress)
    ProgressBar mProgress;

    private String mUserIdOrUid;
    private User mUser;
    private UserInfo mUserInfo;

    private RetainDataFragment mRetainDataFragment;

    private boolean mLoadingUserInfo;

    public static Intent makeIntent(Context context, String userIdOrUid) {
        return new Intent(context, ProfileActivity.class)
                .putExtra(ProfileActivity.EXTRA_USER_ID_OR_UID, userIdOrUid);
    }

    public static Intent makeIntent(Context context, User user) {
        return new Intent(context, ProfileActivity.class)
                .putExtra(ProfileActivity.EXTRA_USER, user);
    }

    public static Intent makeIntent(Context context, UserInfo userInfo) {
        return new Intent(context, ProfileActivity.class)
                .putExtra(ProfileActivity.EXTRA_USER_INFO, userInfo);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        overridePendingTransition(0, 0);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile_activity);
        ButterKnife.bind(this);

        CustomTabsHelperFragment.attachTo(this);
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

        UserInfo userInfo = mRetainDataFragment.remove(RETAIN_DATA_KEY_USER_INFO);
        Intent intent = getIntent();
        if (userInfo == null) {
            userInfo = intent.getParcelableExtra(EXTRA_USER_INFO);
        }
        if (userInfo != null) {
            setUserInfo(userInfo);
        } else {
            mUser = intent.getParcelableExtra(EXTRA_USER);
            if (mUser != null) {
                mUserIdOrUid = String.valueOf(mUser.id);
                mHeaderLayout.bindUser(mUser);
            } else {
                mUserIdOrUid = intent.getStringExtra(EXTRA_USER_ID_OR_UID);
                if (TextUtils.isEmpty(mUserIdOrUid)) {
                    // TODO: Read from uri.
                    //mUserIdOrUid = intent.getData();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mRetainDataFragment.put(RETAIN_DATA_KEY_USER_INFO, mUserInfo);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mUserInfo == null) {
            loadUserInfo();
        }

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
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
            case REQUEST_CODE_LOAD_USER_INFO:
                onLoadUserInfoResponse(successful, (UserInfo) result, error);
                break;
            default:
                LogUtils.w("Unknown request code " + requestCode + ", with successful=" + successful
                        + ", result=" + result + ", error=" + error);
        }
    }

    private void loadUserInfo() {

        if (mLoadingUserInfo) {
            return;
        }

        ApiRequest<UserInfo> request = ApiRequests.newUserInfoRequest(mUserIdOrUid, this);
        RequestFragment.startRequest(REQUEST_CODE_LOAD_USER_INFO, request, null, this);

        setLoadingUserInfo(true);
    }

    private void onLoadUserInfoResponse(boolean successful, UserInfo result, VolleyError error) {

        if (successful) {
            EventBus.getDefault().post(new UserInfoUpdatedEvent(result));
        } else {
            LogUtils.e(error.toString());
            ToastUtils.show(ApiError.getErrorString(error, this), this);
        }

        setLoadingUserInfo(false);
    }

    private void setLoadingUserInfo(boolean loading) {
        mLoadingUserInfo = loading;
        ViewUtils.fadeToVisibility(mProgress, mLoadingUserInfo);
    }

    @Keep
    public void onEventMainThread(UserInfoUpdatedEvent event) {
        UserInfo userInfo = event.userInfo;
        if (TextUtils.equals(mUserIdOrUid, String.valueOf(userInfo.id))
                || TextUtils.equals(mUserIdOrUid, userInfo.uid)) {
            setUserInfo(userInfo);
        }
    }

    private void setUserInfo(UserInfo userInfo) {
        mUserInfo = userInfo;
        mUser = mUserInfo;
        mUserIdOrUid = String.valueOf(mUserInfo.id);
        mHeaderLayout.bindUserInfo(mUserInfo);
    }
}
