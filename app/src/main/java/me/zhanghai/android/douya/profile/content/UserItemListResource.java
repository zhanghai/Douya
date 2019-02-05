/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.profile.content;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.Collections;
import java.util.List;

import me.zhanghai.android.douya.content.RawListResourceFragment;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiService;
import me.zhanghai.android.douya.network.api.info.frodo.UserItemList;
import me.zhanghai.android.douya.network.api.info.frodo.UserItems;
import me.zhanghai.android.douya.util.FragmentUtils;

public class UserItemListResource extends RawListResourceFragment<UserItemList, UserItems> {

    private static final String KEY_PREFIX = UserItemListResource.class.getName() + '.';

    private static final String EXTRA_USER_ID_OR_UID = KEY_PREFIX + "user_id_or_uid";

    private String mUserIdOrUid;

    private static final String FRAGMENT_TAG_DEFAULT = UserItemListResource.class.getName();

    private static UserItemListResource newInstance(String userIdOrUid) {
        //noinspection deprecation
        return new UserItemListResource().setArguments(userIdOrUid);
    }

    public static UserItemListResource attachTo(String userIdOrUid, Fragment fragment, String tag,
                                                int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        UserItemListResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(userIdOrUid);
            FragmentUtils.add(instance, activity, tag);
        }
        instance.setTarget(fragment, requestCode);
        return instance;
    }

    public static UserItemListResource attachTo(String userIdOrUid, Fragment fragment) {
        return attachTo(userIdOrUid, fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public UserItemListResource() {}

    protected UserItemListResource setArguments(String userIdOrUid) {
        FragmentUtils.getArgumentsBuilder(this)
                .putString(EXTRA_USER_ID_OR_UID, userIdOrUid);
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserIdOrUid = getArguments().getString(EXTRA_USER_ID_OR_UID);
    }

    @Override
    protected ApiRequest<UserItemList> onCreateRequest() {
        return ApiService.getInstance().getUserItemList(mUserIdOrUid);
    }

    @Override
    protected void onLoadStarted() {
        getListener().onLoadUserItemListStarted(getRequestCode());
    }

    @Override
    protected void onLoadFinished(boolean successful, UserItemList response, ApiError error) {
        onLoadFinished(successful, response != null ? response.list : null, error);
    }

    private void onLoadFinished(boolean successful, List<UserItems> response, ApiError error) {
        if (successful) {
            set(response);
            getListener().onLoadUserItemListFinished(getRequestCode());
            getListener().onUserItemListChanged(getRequestCode(),
                    Collections.unmodifiableList(response));
        } else {
            getListener().onLoadUserItemListFinished(getRequestCode());
            getListener().onLoadUserItemListError(getRequestCode(), error);
        }
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener {
        void onLoadUserItemListStarted(int requestCode);
        void onLoadUserItemListFinished(int requestCode);
        void onLoadUserItemListError(int requestCode, ApiError error);
        /**
         * @param newUserItemList Unmodifiable.
         */
        void onUserItemListChanged(int requestCode, List<UserItems> newUserItemList);
    }
}
