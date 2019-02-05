/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.user.content;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import me.zhanghai.android.douya.content.ResourceFragment;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.eventbus.UserUpdatedEvent;
import me.zhanghai.android.douya.eventbus.UserWriteFinishedEvent;
import me.zhanghai.android.douya.eventbus.UserWriteStartedEvent;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiService;
import me.zhanghai.android.douya.network.api.info.apiv2.SimpleUser;
import me.zhanghai.android.douya.network.api.info.apiv2.User;
import me.zhanghai.android.douya.util.FragmentUtils;

public class UserResource extends ResourceFragment<User, User> {

    // Not static because we are to be subclassed.
    private final String KEY_PREFIX = getClass().getName() + '.';

    private final String EXTRA_USER_ID_OR_UID = KEY_PREFIX + "user_id_or_uid";
    private final String EXTRA_SIMPLE_USER = KEY_PREFIX + "simple_user";
    private final String EXTRA_USER = KEY_PREFIX + "user";

    private String mUserIdOrUid;
    private SimpleUser mSimpleUser;
    private User mExtraUser;

    private static final String FRAGMENT_TAG_DEFAULT = UserResource.class.getName();

    private static UserResource newInstance(String userIdOrUid, SimpleUser simpleUser, User user) {
        //noinspection deprecation
        return new UserResource().setArguments(userIdOrUid, simpleUser, user);
    }

    public static UserResource attachTo(String userIdOrUid, SimpleUser simpleUser, User user,
                                        Fragment fragment, String tag, int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        UserResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(userIdOrUid, simpleUser, user);
            FragmentUtils.add(instance, activity, tag);
        }
        instance.setTarget(fragment, requestCode);
        return instance;
    }

    public static UserResource attachTo(String userIdOrUid, SimpleUser simpleUser, User user,
                                        Fragment fragment) {
        return attachTo(userIdOrUid, simpleUser, user, fragment, FRAGMENT_TAG_DEFAULT,
                REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public UserResource() {}

    protected UserResource setArguments(String userIdOrUid, SimpleUser simpleUser, User user) {
        FragmentUtils.getArgumentsBuilder(this)
                .putString(EXTRA_USER_ID_OR_UID, userIdOrUid)
                .putParcelable(EXTRA_SIMPLE_USER, simpleUser)
                .putParcelable(EXTRA_USER, user);
        return this;
    }

    public String getUserIdOrUid() {
        ensureArguments();
        return mUserIdOrUid;
    }

    public SimpleUser getSimpleUser() {
        // Can be called before onCreate() is called.
        ensureArguments();
        return mSimpleUser;
    }

    public boolean hasSimpleUser() {
        return getSimpleUser() != null;
    }

    @Override
    public User get() {
        User user = super.get();
        if (user == null) {
            // Can be called before onCreate() is called.
            ensureArguments();
            user = mExtraUser;
        }
        return user;
    }

    @Override
    protected void set(User user) {
        super.set(user);

        user = get();
        if (user != null) {
            mSimpleUser = user;
            mUserIdOrUid = user.getIdOrUid();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ensureArguments();
    }

    private void ensureArguments() {
        if (mUserIdOrUid != null) {
            return;
        }
        Bundle arguments = getArguments();
        mExtraUser = arguments.getParcelable(EXTRA_USER);
        if (mExtraUser != null) {
            mSimpleUser = mExtraUser;
            mUserIdOrUid = mExtraUser.getIdOrUid();
        } else {
            mSimpleUser = arguments.getParcelable(EXTRA_SIMPLE_USER);
            if (mSimpleUser != null) {
                mUserIdOrUid = mSimpleUser.getIdOrUid();
            } else {
                mUserIdOrUid = arguments.getString(EXTRA_USER_ID_OR_UID);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (has()) {
            User user = get();
            setArguments(user.getIdOrUid(), user, user);
        }
    }

    @Override
    protected ApiRequest<User> onCreateRequest() {
        return ApiService.getInstance().getUser(mUserIdOrUid);
    }

    @Override
    protected void onLoadStarted() {
        getListener().onLoadUserStarted(getRequestCode());
    }

    @Override
    protected void onLoadFinished(boolean successful, User response, ApiError error) {
        if (successful) {
            set(response);
            onLoadSuccess(response);
            getListener().onLoadUserFinished(getRequestCode());
            getListener().onUserChanged(getRequestCode(), get());
            EventBusUtils.postAsync(new UserUpdatedEvent(response, this));
        } else {
            getListener().onLoadUserFinished(getRequestCode());
            getListener().onLoadUserError(getRequestCode(), error);
        }
    }

    protected void onLoadSuccess(User user) {}

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUserUpdated(UserUpdatedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        if (event.mUser.isIdOrUid(mUserIdOrUid)) {
            set(event.mUser);
            getListener().onUserChanged(getRequestCode(), get());
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUserWriteStarted(UserWriteStartedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        // Only call listener when we have the data.
        if (mExtraUser != null && mExtraUser.isIdOrUid(event.userIdOrUid)) {
            getListener().onUserWriteStarted(getRequestCode());
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUserWriteFinished(UserWriteFinishedEvent event) {

        if (event.isFromMyself(this)) {
            return;
        }

        // Only call listener when we have the data.
        if (mExtraUser != null && mExtraUser.isIdOrUid(event.userIdOrUid)) {
            getListener().onUserWriteFinished(getRequestCode());
        }
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener {
        void onLoadUserStarted(int requestCode);
        void onLoadUserFinished(int requestCode);
        void onLoadUserError(int requestCode, ApiError error);
        void onUserChanged(int requestCode, User newUser);
        void onUserWriteStarted(int requestCode);
        void onUserWriteFinished(int requestCode);
    }
}
