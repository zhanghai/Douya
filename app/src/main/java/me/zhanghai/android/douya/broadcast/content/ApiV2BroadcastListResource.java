/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.content;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.Collections;
import java.util.List;

import me.zhanghai.android.douya.content.MoreRawListResourceFragment;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiService;
import me.zhanghai.android.douya.network.api.info.apiv2.Broadcast;
import me.zhanghai.android.douya.util.FragmentUtils;

public class ApiV2BroadcastListResource
        extends MoreRawListResourceFragment<List<Broadcast>, Broadcast> {

    private static final String KEY_PREFIX = ApiV2BroadcastListResource.class.getName() + '.';

    private static final String EXTRA_USER_ID_OR_UID = KEY_PREFIX + "user_id_or_uid";
    private static final String EXTRA_TOPIC = KEY_PREFIX + "topic";

    private String mUserIdOrUid;
    private String mTopic;

    private static final String FRAGMENT_TAG_DEFAULT = ApiV2BroadcastListResource.class.getName();

    private static ApiV2BroadcastListResource newInstance(String userIdOrUid, String topic) {
        //noinspection deprecation
        return new ApiV2BroadcastListResource().setArguments(userIdOrUid, topic);
    }

    public static ApiV2BroadcastListResource attachTo(String userIdOrUid, String topic,
                                                      Fragment fragment, String tag,
                                                      int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        ApiV2BroadcastListResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(userIdOrUid, topic);
            FragmentUtils.add(instance, activity, tag);
        }
        instance.setTarget(fragment, requestCode);
        return instance;
    }

    public static ApiV2BroadcastListResource attachTo(String userIdOrUid, String topic,
                                                      Fragment fragment) {
        return attachTo(userIdOrUid, topic, fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public ApiV2BroadcastListResource() {}

    protected ApiV2BroadcastListResource setArguments(String userIdOrUid, String topic) {
        FragmentUtils.getArgumentsBuilder(this)
                .putString(EXTRA_USER_ID_OR_UID, userIdOrUid)
                .putString(EXTRA_TOPIC, topic);
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        mUserIdOrUid = arguments.getString(EXTRA_USER_ID_OR_UID);
        mTopic = arguments.getString(EXTRA_TOPIC);
    }

    @Override
    protected ApiRequest<List<Broadcast>> onCreateRequest(boolean more, int count) {
        Long untilId = null;
        if (more && has()) {
            List<Broadcast> broadcastList = get();
            int size = broadcastList.size();
            if (size > 0) {
                untilId = broadcastList.get(size - 1).id;
            }
        }
        return ApiService.getInstance().getApiV2BroadcastList(mUserIdOrUid, mTopic, untilId, count);
    }

    @Override
    protected ApiRequest<List<Broadcast>> onCreateRequest(Integer start, Integer count) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void onLoadStarted() {
        getListener().onLoadBroadcastListStarted(getRequestCode());
    }

    @Override
    protected void onLoadFinished(boolean more, int count, boolean successful,
                                  List<Broadcast> response, ApiError error) {
        if (successful) {
            if (more) {
                append(response);
                getListener().onLoadBroadcastListFinished(getRequestCode());
                getListener().onBroadcastListAppended(getRequestCode(),
                        Collections.unmodifiableList(response));
            } else {
                setAndNotifyListener(response, true);
            }
        } else {
            getListener().onLoadBroadcastListFinished(getRequestCode());
            getListener().onLoadBroadcastListError(getRequestCode(), error);
        }
    }

    protected void setAndNotifyListener(List<Broadcast> broadcastList, boolean notifyFinished) {
        set(broadcastList);
        if (notifyFinished) {
            getListener().onLoadBroadcastListFinished(getRequestCode());
        }
        getListener().onBroadcastListChanged(getRequestCode(),
                Collections.unmodifiableList(get()));
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener {
        void onLoadBroadcastListStarted(int requestCode);
        void onLoadBroadcastListFinished(int requestCode);
        void onLoadBroadcastListError(int requestCode, ApiError error);
        /**
         * @param newBroadcastList Unmodifiable.
         */
        void onBroadcastListChanged(int requestCode, List<Broadcast> newBroadcastList);
        /**
         * @param appendedBroadcastList Unmodifiable.
         */
        void onBroadcastListAppended(int requestCode, List<Broadcast> appendedBroadcastList);
    }
}
