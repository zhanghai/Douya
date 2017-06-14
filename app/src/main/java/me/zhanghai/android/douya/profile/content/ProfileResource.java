/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.profile.content;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.util.List;

import me.zhanghai.android.douya.app.TargetedRetainedFragment;
import me.zhanghai.android.douya.broadcast.content.BroadcastListResource;
import me.zhanghai.android.douya.diary.content.UserDiaryListResource;
import me.zhanghai.android.douya.followship.content.FollowingListResource;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.info.apiv2.Broadcast;
import me.zhanghai.android.douya.network.api.info.apiv2.SimpleUser;
import me.zhanghai.android.douya.network.api.info.apiv2.User;
import me.zhanghai.android.douya.network.api.info.frodo.Diary;
import me.zhanghai.android.douya.network.api.info.frodo.Review;
import me.zhanghai.android.douya.network.api.info.frodo.UserItems;
import me.zhanghai.android.douya.review.content.UserReviewListResource;
import me.zhanghai.android.douya.user.content.UserResource;
import me.zhanghai.android.douya.util.FragmentUtils;

public class ProfileResource extends TargetedRetainedFragment implements UserResource.Listener,
        BroadcastListResource.Listener, FollowingListResource.Listener,
        UserDiaryListResource.Listener, UserItemListResource.Listener,
        UserReviewListResource.Listener {

    private static final String KEY_PREFIX = ProfileResource.class.getName() + '.';

    private static final String EXTRA_USER_ID_OR_UID = KEY_PREFIX + "user_id_or_uid";
    private static final String EXTRA_SIMPLE_USER = KEY_PREFIX + "simple_user";
    private static final String EXTRA_USER = KEY_PREFIX + "user";

    private String mUserIdOrUid;
    private SimpleUser mSimpleUser;

    private User mUser;

    private UserResource mUserResource;
    private BroadcastListResource mBroadcastListResource;
    private FollowingListResource mFollowingListResource;
    private UserDiaryListResource mDiaryListResource;
    private UserItemListResource mUserItemListResource;
    private UserReviewListResource mReviewListResource;

    private boolean mHasError;

    private static final String FRAGMENT_TAG_DEFAULT = ProfileResource.class.getName();

    private static ProfileResource newInstance(String userIdOrUid, SimpleUser simpleUser, User user) {
        //noinspection deprecation
        return new ProfileResource().setArguments(userIdOrUid, simpleUser, user);
    }

    public static ProfileResource attachTo(String userIdOrUid, SimpleUser simpleUser, User user,
                                           Fragment fragment, String tag, int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        ProfileResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(userIdOrUid, simpleUser, user);
            instance.targetAt(fragment, requestCode);
            FragmentUtils.add(instance, activity, tag);
        }
        return instance;
    }

    public static ProfileResource attachTo(String userIdOrUid, SimpleUser simpleUser, User user,
                                           Fragment fragment) {
        return attachTo(userIdOrUid, simpleUser, user, fragment, FRAGMENT_TAG_DEFAULT,
                REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public ProfileResource() {}

    protected ProfileResource setArguments(String userIdOrUid, SimpleUser simpleUser, User user) {
        Bundle arguments = FragmentUtils.ensureArguments(this);
        arguments.putString(EXTRA_USER_ID_OR_UID, userIdOrUid);
        arguments.putParcelable(EXTRA_SIMPLE_USER, simpleUser);
        arguments.putParcelable(EXTRA_USER, user);
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ensureArguments();

        mUserResource = UserResource.attachTo(mUserIdOrUid, mSimpleUser, mUser, this);
        ensureResourcesIfHasSimpleUser();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mUserResource.detach();
        if (mBroadcastListResource != null) {
            mBroadcastListResource.detach();
        }
        if (mFollowingListResource != null) {
            mFollowingListResource.detach();
        }
        if (mDiaryListResource != null) {
            mDiaryListResource.detach();
        }
        if (mUserItemListResource != null) {
            mUserItemListResource.detach();
        }
        if (mReviewListResource != null) {
            mReviewListResource.detach();
        }

        Bundle arguments = getArguments();
        arguments.putString(EXTRA_USER_ID_OR_UID, mUserIdOrUid);
        arguments.putParcelable(EXTRA_SIMPLE_USER, mSimpleUser);
        arguments.putParcelable(EXTRA_USER, mUser);
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
        // Can be called before onCreate() is called.
        ensureArguments();
        return mSimpleUser != null;
    }

    public User getUser() {
        // Can be called before onCreate() is called.
        ensureArguments();
        return mUser;
    }

    public boolean hasUser() {
        // Can be called before onCreate() is called.
        ensureArguments();
        return mUser != null;
    }

    private void ensureArguments() {
        if (mUserIdOrUid != null) {
            return;
        }
        Bundle arguments = getArguments();
        mUser = arguments.getParcelable(EXTRA_USER);
        if (mUser != null) {
            mSimpleUser = mUser;
            mUserIdOrUid = mUser.getIdOrUid();
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
    public void onLoadUserStarted(int requestCode) {}

    @Override
    public void onLoadUserFinished(int requestCode) {}

    @Override
    public void onLoadUserError(int requestCode, ApiError error) {
        notifyError(error);
    }

    @Override
    public void onUserChanged(int requestCode, User newUser) {
        mUser = newUser;
        mSimpleUser = newUser;
        mUserIdOrUid = newUser.getIdOrUid();
        getListener().onUserChanged(getRequestCode(), newUser);
        notifyChangedIfLoaded();
        ensureResourcesIfHasSimpleUser();
    }

    @Override
    public void onUserWriteStarted(int requestCode) {
        getListener().onUserWriteStarted(getRequestCode());
    }

    @Override
    public void onUserWriteFinished(int requestCode) {
        getListener().onUserWriteFinished(getRequestCode());
    }

    private void ensureResourcesIfHasSimpleUser() {
        if (mBroadcastListResource != null || mFollowingListResource != null
                || mDiaryListResource != null || mUserItemListResource != null
                || mReviewListResource != null) {
            return;
        }
        if (mSimpleUser == null) {
            return;
        }
        mBroadcastListResource = BroadcastListResource.attachTo(mSimpleUser.getIdOrUid(), null, this);
        mFollowingListResource = FollowingListResource.attachTo(mSimpleUser.getIdOrUid(), this);
        mDiaryListResource = UserDiaryListResource.attachTo(mSimpleUser.getIdOrUid(), this);
        mUserItemListResource = UserItemListResource.attachTo(mSimpleUser.getIdOrUid(), this);
        mReviewListResource = UserReviewListResource.attachTo(mSimpleUser.getIdOrUid(), this);
    }

    @Override
    public void onLoadBroadcastListStarted(int requestCode) {}

    @Override
    public void onLoadBroadcastListFinished(int requestCode) {}

    @Override
    public void onLoadBroadcastListError(int requestCode, ApiError error) {
        notifyError(error);
    }

    @Override
    public void onBroadcastListChanged(int requestCode, List<Broadcast> newBroadcastList) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onBroadcastListAppended(int requestCode, List<Broadcast> appendedBroadcastList) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onBroadcastChanged(int requestCode, int position, Broadcast newBroadcast) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onBroadcastRemoved(int requestCode, int position) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onBroadcastWriteStarted(int requestCode, int position) {}

    @Override
    public void onBroadcastWriteFinished(int requestCode, int position) {}

    @Override
    public void onLoadUserListStarted(int requestCode) {}

    @Override
    public void onLoadUserListFinished(int requestCode) {}

    @Override
    public void onLoadUserListError(int requestCode, ApiError error) {
        notifyError(error);
    }

    @Override
    public void onUserListChanged(int requestCode, List<SimpleUser> newUserList) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onUserListAppended(int requestCode, List<SimpleUser> appendedUserList) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onLoadDiaryListStarted(int requestCode) {}

    @Override
    public void onLoadDiaryListFinished(int requestCode) {}

    @Override
    public void onLoadDiaryListError(int requestCode, ApiError error) {
        notifyError(error);
    }

    @Override
    public void onDiaryListChanged(int requestCode, List<Diary> newDiaryList) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onDiaryListAppended(int requestCode, List<Diary> appendedDiaryList) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onDiaryChanged(int requestCode, int position, Diary newDiary) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onDiaryRemoved(int requestCode, int position) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onLoadUserItemListStarted(int requestCode) {}

    @Override
    public void onLoadUserItemListFinished(int requestCode) {}

    @Override
    public void onLoadUserItemListError(int requestCode, ApiError error) {
        notifyError(error);
    }

    @Override
    public void onUserItemListChanged(int requestCode, List<UserItems> newUserItemList) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onLoadReviewListStarted(int requestCode) {}

    @Override
    public void onLoadReviewListFinished(int requestCode) {}

    @Override
    public void onLoadReviewListError(int requestCode, ApiError error) {
        notifyError(error);
    }

    @Override
    public void onReviewListChanged(int requestCode, List<Review> newReviewList) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onReviewListAppended(int requestCode, List<Review> appendedReviewList) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onReviewChanged(int requestCode, int position, Review newReview) {
        notifyChangedIfLoaded();
    }

    @Override
    public void onReviewRemoved(int requestCode, int position) {
        notifyChangedIfLoaded();
    }

    public boolean isLoaded() {
        return hasUser() && mBroadcastListResource != null && mBroadcastListResource.has()
                && mFollowingListResource != null && mFollowingListResource.has()
                && mDiaryListResource != null && mDiaryListResource.has()
                && mUserItemListResource != null && mUserItemListResource.has()
                && mReviewListResource != null && mReviewListResource.has();
    }

    public void notifyChangedIfLoaded() {
        if (isLoaded()) {
            getListener().onChanged(getRequestCode(), getUser(), mBroadcastListResource.get(),
                    mFollowingListResource.get(), mDiaryListResource.get(),
                    mUserItemListResource.get(), mReviewListResource.get());
        }
    }

    private void notifyError(ApiError error) {
        if (!mHasError) {
            mHasError = true;
            getListener().onLoadError(getRequestCode(), error);
        }
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener {
        void onLoadError(int requestCode, ApiError error);
        void onUserChanged(int requestCode, User newUser);
        void onUserWriteStarted(int requestCode);
        void onUserWriteFinished(int requestCode);
        void onChanged(int requestCode, User newUser, List<Broadcast> newBroadcastList,
                       List<SimpleUser> newFollowingList, List<Diary> newDiaryList,
                       List<UserItems> newUserItemList, List<Review> newReviewList);
    }
}
