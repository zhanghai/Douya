/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.content;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiService;
import me.zhanghai.android.douya.network.api.info.frodo.LikerList;
import me.zhanghai.android.douya.user.content.BaseUserListResource;
import me.zhanghai.android.douya.util.FragmentUtils;

public class BroadcastLikerListResource extends BaseUserListResource<LikerList> {

    private static final String FRAGMENT_TAG_DEFAULT = BroadcastLikerListResource.class.getName();

    private final String KEY_PREFIX = BroadcastLikerListResource.class.getName() + '.';

    private final String EXTRA_BROADCAST_ID = KEY_PREFIX + "broadcast_id";

    private long mBroadcastId;

    private static BroadcastLikerListResource newInstance(long broadcastId) {
        //noinspection deprecation
        return new BroadcastLikerListResource().setArguments(broadcastId);
    }

    public static BroadcastLikerListResource attachTo(long broadcastId, Fragment fragment,
                                                      String tag, int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        BroadcastLikerListResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(broadcastId);
            FragmentUtils.add(instance, activity, tag);
        }
        instance.setTarget(fragment, requestCode);
        return instance;
    }

    public static BroadcastLikerListResource attachTo(long broadcastId, Fragment fragment) {
        return attachTo(broadcastId, fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public BroadcastLikerListResource() {}

    protected BroadcastLikerListResource setArguments(long broadcastId) {
        FragmentUtils.getArgumentsBuilder(this)
                .putLong(EXTRA_BROADCAST_ID, broadcastId);
        return this;
    }

    protected long getBroadcastId() {
        return mBroadcastId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBroadcastId = getArguments().getLong(EXTRA_BROADCAST_ID);
    }

    @Override
    protected ApiRequest<LikerList> onCreateRequest(Integer start, Integer count) {
        return ApiService.getInstance().getBroadcastLikerList(getBroadcastId(), start, count);
    }

    @Override
    protected void onCallRawLoadFinished(boolean more, int count, boolean successful,
                                         LikerList response, ApiError error) {
        onRawLoadFinished(more, count, successful, successful ? response.likers : null, error);
    }
}
