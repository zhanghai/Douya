/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.content;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;

import me.zhanghai.android.douya.content.MoreBaseListResourceFragment;
import me.zhanghai.android.douya.eventbus.BroadcastDeletedEvent;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiService;
import me.zhanghai.android.douya.network.api.info.frodo.RebroadcastItem;
import me.zhanghai.android.douya.network.api.info.frodo.RebroadcastList;
import me.zhanghai.android.douya.util.FragmentUtils;

public class BroadcastRebroadcastListResource
        extends MoreBaseListResourceFragment<RebroadcastList, RebroadcastItem> {

    private static final String FRAGMENT_TAG_DEFAULT =
            BroadcastRebroadcastListResource.class.getName();

    private final String KEY_PREFIX = BroadcastRebroadcastListResource.class.getName()
            + '.';

    private final String EXTRA_BROADCAST_ID = KEY_PREFIX + "broadcast_id";

    private long mBroadcastId;

    private static BroadcastRebroadcastListResource newInstance(long broadcastId) {
        //noinspection deprecation
        return new BroadcastRebroadcastListResource().setArguments(broadcastId);
    }

    public static BroadcastRebroadcastListResource attachTo(long broadcastId,
                                                            Fragment fragment,
                                                            String tag,
                                                            int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        BroadcastRebroadcastListResource instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            instance = newInstance(broadcastId);
            FragmentUtils.add(instance, activity, tag);
        }
        instance.setTarget(fragment, requestCode);
        return instance;
    }

    public static BroadcastRebroadcastListResource attachTo(long broadcastId,
                                                            Fragment fragment) {
        return attachTo(broadcastId, fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public BroadcastRebroadcastListResource() {}

    protected BroadcastRebroadcastListResource setArguments(long broadcastId) {
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
    protected ApiRequest<RebroadcastList> onCreateRequest(Integer start, Integer count) {
        return ApiService.getInstance().getBroadcastRebroadcastList(mBroadcastId, start, count);
    }

    @Override
    protected void onLoadStarted() {
        getListener().onLoadRebroadcastListStarted(getRequestCode());
    }

    @Override
    protected void onLoadFinished(boolean more, int count, boolean successful,
                                  List<RebroadcastItem> response, ApiError error) {
        if (successful) {
            if (more) {
                append(response);
                getListener().onLoadRebroadcastListFinished(getRequestCode());
                getListener().onRebroadcastListAppended(getRequestCode(),
                        Collections.unmodifiableList(response));
            } else {
                set(response);
                getListener().onLoadRebroadcastListFinished(getRequestCode());
                getListener().onRebroadcastListChanged(getRequestCode(),
                        Collections.unmodifiableList(get()));
            }
        } else {
            getListener().onLoadRebroadcastListFinished(getRequestCode());
            getListener().onLoadRebroadcastListError(getRequestCode(), error);
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onBroadcastDeleted(BroadcastDeletedEvent event) {

        if (event.isFromMyself(this) || isEmpty()) {
            return;
        }

        List<RebroadcastItem> rebroadcastList = get();
        for (int i = 0, size = rebroadcastList.size(); i < size; ) {
            RebroadcastItem rebroadcastItem = rebroadcastList.get(i);
            if (rebroadcastItem.getBroadcastId() == event.broadcastId) {
                rebroadcastList.remove(i);
                getListener().onRebroadcastItemRemoved(getRequestCode(), i);
                --size;
            }
        }
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener {
        void onLoadRebroadcastListStarted(int requestCode);
        void onLoadRebroadcastListFinished(int requestCode);
        void onLoadRebroadcastListError(int requestCode, ApiError error);
        /**
         * @param newRebroadcastList Unmodifiable.
         */
        void onRebroadcastListChanged(int requestCode, List<RebroadcastItem> newRebroadcastList);
        /**
         * @param appendedRebroadcastList Unmodifiable.
         */
        void onRebroadcastListAppended(int requestCode,
                                       List<RebroadcastItem> appendedRebroadcastList);
        void onRebroadcastItemRemoved(int requestCode, int position);
    }
}
