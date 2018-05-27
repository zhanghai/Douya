/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.content;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiService;
import me.zhanghai.android.douya.network.api.info.frodo.BroadcastList;
import me.zhanghai.android.douya.util.FragmentUtils;

public class BroadcastRebroadcastedBroadcastListResource extends BaseBroadcastListResource {

    private static final String FRAGMENT_TAG_DEFAULT =
            BroadcastRebroadcastedBroadcastListResource.class.getName();

    private final String KEY_PREFIX = BroadcastRebroadcastedBroadcastListResource.class.getName()
            + '.';

    private final String EXTRA_BROADCAST_ID = KEY_PREFIX + "broadcast_id";

    private long mBroadcastId;

    private static BroadcastRebroadcastedBroadcastListResource newInstance(long broadcastId) {
        //noinspection deprecation
        return new BroadcastRebroadcastedBroadcastListResource().setArguments(broadcastId);
    }

    public static BroadcastRebroadcastedBroadcastListResource attachTo(long broadcastId,
                                                                       Fragment fragment,
                                                                       String tag,
                                                                       int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        BroadcastRebroadcastedBroadcastListResource instance = FragmentUtils.findByTag(activity,
                tag);
        if (instance == null) {
            instance = newInstance(broadcastId);
            FragmentUtils.add(instance, activity, tag);
        }
        instance.setTarget(fragment, requestCode);
        return instance;
    }

    public static BroadcastRebroadcastedBroadcastListResource attachTo(long broadcastId,
                                                                       Fragment fragment) {
        return attachTo(broadcastId, fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public BroadcastRebroadcastedBroadcastListResource() {}

    protected BroadcastRebroadcastedBroadcastListResource setArguments(long broadcastId) {
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
    protected ApiRequest<BroadcastList> onCreateRequest(Integer start, Integer count) {
        return ApiService.getInstance().getBroadcastRebroadcastedBroadcastList(mBroadcastId, start,
                count);
    }

    @Override
    protected boolean shouldPostBroadcastUpdatedEvent() {
        // Our broadcast is incomplete.
        return false;
    }
}
