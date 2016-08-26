/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.user.content;

import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.android.volley.VolleyError;

import me.zhanghai.android.douya.content.ResourceFragment;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.eventbus.UserInfoUpdatedEvent;
import me.zhanghai.android.douya.eventbus.UserInfoWriteFinishedEvent;
import me.zhanghai.android.douya.eventbus.UserInfoWriteStartedEvent;
import me.zhanghai.android.douya.network.RequestFragment;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiRequests;
import me.zhanghai.android.douya.network.api.info.apiv2.User;
import me.zhanghai.android.douya.network.api.info.apiv2.UserInfo;
import me.zhanghai.android.douya.util.FragmentUtils;

public class UserInfoResource extends ResourceFragment
        implements RequestFragment.Listener<UserInfo, Void> {

    // Not static because we are to be subclassed.
    private final String KEY_PREFIX = getClass().getName() + '.';

    private final String EXTRA_USER_ID_OR_UID = KEY_PREFIX + "user_id_or_uid";
    private final String EXTRA_USER = KEY_PREFIX + "user";
    private final String EXTRA_USER_INFO = KEY_PREFIX + "user_info";

    private String mUserIdOrUid;
    private User mUser;

    private UserInfo mUserInfo;

    private boolean mLoading;

    private static final String FRAGMENT_TAG_DEFAULT = UserInfoResource.class.getName();

    private static UserInfoResource newInstance(String userIdOrUid, User user, UserInfo userInfo) {
        //noinspection deprecation
        UserInfoResource resource = new UserInfoResource();
        resource.setArguments(userIdOrUid, user, userInfo);
        return resource;
    }

    public static UserInfoResource attachTo(String userIdOrUid, User user, UserInfo userInfo,
                                            FragmentActivity activity, String tag,
                                            int requestCode) {
        return attachTo(userIdOrUid, user, userInfo, activity, tag, true, null, requestCode);
    }

    public static UserInfoResource attachTo(String userIdOrUid, User user, UserInfo userInfo,
                                            FragmentActivity activity) {
        return attachTo(userIdOrUid, user, userInfo, activity, FRAGMENT_TAG_DEFAULT,
                REQUEST_CODE_INVALID);
    }

    public static UserInfoResource attachTo(String userIdOrUid, User user, UserInfo userInfo,
                                            Fragment fragment, String tag, int requestCode) {
        return attachTo(userIdOrUid, user, userInfo, fragment.getActivity(), tag, false, fragment,
                requestCode);
    }

    public static UserInfoResource attachTo(String userIdOrUid, User user, UserInfo userInfo,
                                            Fragment fragment) {
        return attachTo(userIdOrUid, user, userInfo, fragment, FRAGMENT_TAG_DEFAULT,
                REQUEST_CODE_INVALID);
    }

    private static UserInfoResource attachTo(String userIdOrUid, User user, UserInfo userInfo,
                                             FragmentActivity activity, String tag,
                                             boolean targetAtActivity, Fragment targetFragment,
                                             int requestCode) {
        UserInfoResource resource = FragmentUtils.findByTag(activity, tag);
        if (resource == null) {
            resource = newInstance(userIdOrUid, user, userInfo);
            if (targetAtActivity) {
                resource.targetAtActivity(requestCode);
            } else {
                resource.targetAtFragment(targetFragment, requestCode);
            }
            FragmentUtils.add(resource, activity, tag);
        }
        return resource;
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public UserInfoResource() {}

    protected void setArguments(String userIdOrUid, User user, UserInfo userInfo) {
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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ensureUserInfoAndUserAndIdOrUidFromArguments();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Bundle arguments = getArguments();
        arguments.putString(EXTRA_USER_ID_OR_UID, mUserIdOrUid);
        arguments.putParcelable(EXTRA_USER, mUser);
        arguments.putParcelable(EXTRA_USER_INFO, mUserInfo);
    }

    public String getUserIdOrUid() {
        ensureUserInfoAndUserAndIdOrUidFromArguments();
        return mUserIdOrUid;
    }

    public User getUser() {
        // Can be called before onCreate() is called.
        ensureUserInfoAndUserAndIdOrUidFromArguments();
        return mUser;
    }

    public boolean hasUser() {
        // Can be called before onCreate() is called.
        ensureUserInfoAndUserAndIdOrUidFromArguments();
        return mUser != null;
    }

    public UserInfo getUserInfo() {
        // Can be called before onCreate() is called.
        ensureUserInfoAndUserAndIdOrUidFromArguments();
        return mUserInfo;
    }

    public boolean hasUserInfo() {
        // Can be called before onCreate() is called.
        ensureUserInfoAndUserAndIdOrUidFromArguments();
        return mUserInfo != null;
    }

    public boolean isLoading() {
        return mLoading;
    }

    private void ensureUserInfoAndUserAndIdOrUidFromArguments() {
        if (mUserIdOrUid == null) {
            Bundle arguments = getArguments();
            mUserIdOrUid = arguments.getString(EXTRA_USER_ID_OR_UID);
            mUser = arguments.getParcelable(EXTRA_USER);
            mUserInfo = arguments.getParcelable(EXTRA_USER_INFO);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        EventBusUtils.register(this);

        loadOnStart();
    }

    protected void loadOnStart() {
        if (mUserInfo == null) {
            load();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBusUtils.unregister(this);
    }

    public void load() {

        if (mLoading) {
            return;
        }

        mLoading = true;
        getListener().onLoadUserInfoStarted(getRequestCode());

        ApiRequest<UserInfo> request = ApiRequests.newUserInfoRequest(mUserIdOrUid);
        RequestFragment.startRequest(request, null, this);
    }

    @Override
    public void onVolleyResponse(int requestCode, final boolean successful,
                                 final UserInfo result, final VolleyError error,
                                 final Void requestState) {
        postOnResumed(new Runnable() {
            @Override
            public void run() {
                onLoadFinished(successful, result, error);
            }
        });
    }

    private void onLoadFinished(boolean successful, UserInfo userInfo, VolleyError error) {

        mLoading = false;
        getListener().onLoadUserInfoFinished(getRequestCode());

        if (successful) {
            setUserInfo(userInfo);
            onUserInfoLoaded(userInfo);
            EventBusUtils.postAsync(new UserInfoUpdatedEvent(mUserInfo, this));
        } else {
            getListener().onLoadUserInfoError(getRequestCode(), error);
        }
    }

    protected void onUserInfoLoaded(UserInfo userInfo) {}

    @Keep
    public void onEventMainThread(UserInfoUpdatedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        if (event.userInfo.hasIdOrUid(mUserIdOrUid)) {
            setUserInfo(event.userInfo);
        }
    }

    private void setUserInfo(UserInfo userInfo) {
        mUserInfo = userInfo;
        mUser = mUserInfo;
        mUserIdOrUid = mUserInfo.getIdOrUid();
        getListener().onUserInfoChanged(getRequestCode(), mUserInfo);
    }

    @Keep
    public void onEventMainThread(UserInfoWriteStartedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        // Only call listener when we have the data.
        if (mUserInfo != null && mUserInfo.hasIdOrUid(event.userIdOrUid)) {
            getListener().onUserInfoWriteStarted(getRequestCode());
        }
    }

    @Keep
    public void onEventMainThread(UserInfoWriteFinishedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        // Only call listener when we have the data.
        if (mUserInfo != null && mUserInfo.hasIdOrUid(event.userIdOrUid)) {
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
