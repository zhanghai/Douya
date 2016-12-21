/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.user.content;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.android.volley.VolleyError;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.zhanghai.android.douya.content.ResourceFragment;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.eventbus.UserInfoUpdatedEvent;
import me.zhanghai.android.douya.eventbus.UserInfoWriteFinishedEvent;
import me.zhanghai.android.douya.eventbus.UserInfoWriteStartedEvent;
import me.zhanghai.android.douya.network.Request;
import me.zhanghai.android.douya.network.api.ApiRequests;
import me.zhanghai.android.douya.network.api.info.apiv2.User;
import me.zhanghai.android.douya.network.api.info.apiv2.UserInfo;
import me.zhanghai.android.douya.util.FragmentUtils;

public class UserInfoResource extends ResourceFragment<UserInfo, UserInfo> {

    // Not static because we are to be subclassed.
    private final String KEY_PREFIX = getClass().getName() + '.';

    private final String EXTRA_USER_ID_OR_UID = KEY_PREFIX + "user_id_or_uid";
    private final String EXTRA_USER = KEY_PREFIX + "user";
    private final String EXTRA_USER_INFO = KEY_PREFIX + "user_info";

    private String mUserIdOrUid;
    private User mUser;
    private UserInfo mExtraUserInfo;

    private static final String FRAGMENT_TAG_DEFAULT = UserInfoResource.class.getName();

    private static UserInfoResource newInstance(String userIdOrUid, User user, UserInfo userInfo) {
        //noinspection deprecation
        return new UserInfoResource().setArguments(userIdOrUid, user, userInfo);
    }

    public static UserInfoResource attachTo(String userIdOrUid, User user, UserInfo userInfo,
                                            Fragment fragment, String tag, int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        UserInfoResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(userIdOrUid, user, userInfo);
            instance.targetAt(fragment, requestCode);
            FragmentUtils.add(instance, activity, tag);
        }
        return instance;
    }

    public static UserInfoResource attachTo(String userIdOrUid, User user, UserInfo userInfo,
                                            Fragment fragment) {
        return attachTo(userIdOrUid, user, userInfo, fragment, FRAGMENT_TAG_DEFAULT,
                REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public UserInfoResource() {}

    protected UserInfoResource setArguments(String userIdOrUid, User user, UserInfo userInfo) {
        Bundle arguments = FragmentUtils.ensureArguments(this);
        if (userInfo != null) {
            arguments.putString(EXTRA_USER_ID_OR_UID, userInfo.getIdOrUid());
            arguments.putParcelable(EXTRA_USER, userInfo);
            arguments.putParcelable(EXTRA_USER_INFO, userInfo);
        } else if (user != null) {
            arguments.putString(EXTRA_USER_ID_OR_UID, user.getIdOrUid());
            arguments.putParcelable(EXTRA_USER, user);
        } else {
            arguments.putString(EXTRA_USER_ID_OR_UID, userIdOrUid);
        }
        return this;
    }

    public String getUserIdOrUid() {
        ensureArguments();
        return mUserIdOrUid;
    }

    public User getUser() {
        // Can be called before onCreate() is called.
        ensureArguments();
        return mUser;
    }

    public boolean hasUser() {
        return getUser() != null;
    }

    @Override
    public UserInfo get() {
        UserInfo userInfo = super.get();
        if (userInfo == null) {
            // Can be called before onCreate() is called.
            ensureArguments();
            userInfo = mExtraUserInfo;
        }
        return userInfo;
    }

    @Override
    protected void set(UserInfo userInfo) {
        super.set(userInfo);

        userInfo = get();
        if (userInfo != null) {
            mUser = userInfo;
            mUserIdOrUid = userInfo.getIdOrUid();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ensureArguments();
    }

    private void ensureArguments() {
        if (mUserIdOrUid == null) {
            Bundle arguments = getArguments();
            mUserIdOrUid = arguments.getString(EXTRA_USER_ID_OR_UID);
            mUser = arguments.getParcelable(EXTRA_USER);
            mExtraUserInfo = arguments.getParcelable(EXTRA_USER_INFO);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Bundle arguments = getArguments();
        arguments.putString(EXTRA_USER_ID_OR_UID, mUserIdOrUid);
        arguments.putParcelable(EXTRA_USER, mUser);
        arguments.putParcelable(EXTRA_USER_INFO, mExtraUserInfo);
    }

    @Override
    protected Request<UserInfo> onCreateRequest() {
        return ApiRequests.newUserInfoRequest(mUserIdOrUid);
    }

    @Override
    protected void onLoadStarted() {
        getListener().onLoadUserInfoStarted(getRequestCode());
    }

    @Override
    protected void onLoadFinished(boolean successful, UserInfo response, VolleyError error) {
        getListener().onLoadUserInfoFinished(getRequestCode());
        if (successful) {
            set(response);
            onLoadSuccess(response);
            getListener().onUserInfoChanged(getRequestCode(), get());
            EventBusUtils.postAsync(new UserInfoUpdatedEvent(response, this));
        } else {
            getListener().onLoadUserInfoError(getRequestCode(), error);
        }
    }

    protected void onLoadSuccess(UserInfo userInfo) {}

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserInfoUpdated(UserInfoUpdatedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        if (event.userInfo.hasIdOrUid(mUserIdOrUid)) {
            set(event.userInfo);
            getListener().onUserInfoChanged(getRequestCode(), get());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserInfoWriteStarted(UserInfoWriteStartedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        // Only call listener when we have the data.
        if (mExtraUserInfo != null && mExtraUserInfo.hasIdOrUid(event.userIdOrUid)) {
            getListener().onUserInfoWriteStarted(getRequestCode());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserInfoWriteFinished(UserInfoWriteFinishedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        // Only call listener when we have the data.
        if (mExtraUserInfo != null && mExtraUserInfo.hasIdOrUid(event.userIdOrUid)) {
            getListener().onUserInfoWriteFinished(getRequestCode());
        }
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener {
        void onLoadUserInfoStarted(int requestCode);
        void onLoadUserInfoFinished(int requestCode);
        void onLoadUserInfoError(int requestCode, VolleyError error);
        void onUserInfoChanged(int requestCode, UserInfo newUserInfo);
        void onUserInfoWriteStarted(int requestCode);
        void onUserInfoWriteFinished(int requestCode);
    }
}
