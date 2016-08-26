/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.profile.content;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.android.volley.VolleyError;

import java.util.Collections;
import java.util.List;

import me.zhanghai.android.douya.content.ResourceFragment;
import me.zhanghai.android.douya.network.RequestFragment;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiRequests;
import me.zhanghai.android.douya.network.api.info.frodo.UserItemList;
import me.zhanghai.android.douya.network.api.info.frodo.UserItems;
import me.zhanghai.android.douya.util.FragmentUtils;

public class UserItemListResource extends ResourceFragment
        implements RequestFragment.Listener<UserItemList, Void> {

    private static final String KEY_PREFIX = UserItemListResource.class.getName() + '.';

    private static final String EXTRA_USER_ID_OR_UID = KEY_PREFIX + "user_id_or_uid";

    private String mUserIdOrUid;

    private List<UserItems> mUserItemList;

    private boolean mLoading;

    private static final String FRAGMENT_TAG_DEFAULT = UserItemListResource.class.getName();

    private static UserItemListResource newInstance(String userIdOrUid) {
        //noinspection deprecation
        UserItemListResource resource = new UserItemListResource();
        resource.setArguments(userIdOrUid);
        return resource;
    }

    public static UserItemListResource attachTo(String userIdOrUid, FragmentActivity activity,
                                                String tag, int requestCode) {
        return attachTo(userIdOrUid, activity, tag, true, null, requestCode);
    }

    public static UserItemListResource attachTo(String userIdOrUid, FragmentActivity activity) {
        return attachTo(userIdOrUid, activity, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    public static UserItemListResource attachTo(String userIdOrUid, Fragment fragment, String tag,
                                                int requestCode) {
        return attachTo(userIdOrUid, fragment.getActivity(), tag, false, fragment, requestCode);
    }

    public static UserItemListResource attachTo(String userIdOrUid, Fragment fragment) {
        return attachTo(userIdOrUid, fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    private static UserItemListResource attachTo(String userIdOrUid, FragmentActivity activity,
                                                 String tag, boolean targetAtActivity,
                                                 Fragment targetFragment, int requestCode) {
        UserItemListResource resource = FragmentUtils.findByTag(activity, tag);
        if (resource == null) {
            resource = newInstance(userIdOrUid);
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
    public UserItemListResource() {}

    protected void setArguments(String userIdOrUid) {
        FragmentUtils.ensureArguments(this)
                .putString(EXTRA_USER_ID_OR_UID, userIdOrUid);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserIdOrUid = getArguments().getString(EXTRA_USER_ID_OR_UID);
    }

    /**
     * @return Unmodifiable user item list, or {@code null}.
     */
    public List<UserItems> get() {
        return mUserItemList != null ? Collections.unmodifiableList(mUserItemList) : null;
    }

    public boolean has() {
        return mUserItemList != null;
    }

    public boolean isEmpty() {
        return mUserItemList == null || mUserItemList.isEmpty();
    }

    public boolean isLoading() {
        return mLoading;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mUserItemList == null) {
            load();
        }
    }

    public void load() {

        if (mLoading) {
            return;
        }

        mLoading = true;
        getListener().onLoadUserItemListStarted(getRequestCode());

        ApiRequest<UserItemList> request = ApiRequests.newUserItemListRequest(mUserIdOrUid);
        RequestFragment.startRequest(request, null, this);
    }

    @Override
    public void onVolleyResponse(int requestCode, final boolean successful,
                                 final UserItemList result, final VolleyError error,
                                 Void requestState) {
        postOnResumed(new Runnable() {
            @Override
            public void run() {
                onLoadFinished(successful, result != null ? result.list : null, error);
            }
        });
    }

    private void onLoadFinished(boolean successful, List<UserItems> userItemList,
                                VolleyError error) {

        mLoading = false;
        getListener().onLoadUserItemListFinished(getRequestCode());

        if (successful) {
            mUserItemList = userItemList;
            getListener().onUserItemListChanged(getRequestCode(),
                    Collections.unmodifiableList(userItemList));
        } else {
            getListener().onLoadUserItemListError(getRequestCode(), error);
        }
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener {
        void onLoadUserItemListStarted(int requestCode);
        void onLoadUserItemListFinished(int requestCode);
        void onLoadUserItemListError(int requestCode, VolleyError error);
        /**
         * @param newUserItemList Unmodifiable.
         */
        void onUserItemListChanged(int requestCode, List<UserItems> newUserItemList);
    }
}
