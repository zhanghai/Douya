/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.doulist.content;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;

import me.zhanghai.android.douya.content.MoreBaseListResourceFragment;
import me.zhanghai.android.douya.eventbus.DoulistDeletedEvent;
import me.zhanghai.android.douya.eventbus.DoulistUpdatedEvent;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.info.frodo.Doulist;
import me.zhanghai.android.douya.network.api.info.frodo.DoulistList;

public abstract class BaseDoulistResource
        extends MoreBaseListResourceFragment<DoulistList, Doulist> {

    @Override
    protected void onLoadStarted() {
        getListener().onLoadDoulistListStarted(getRequestCode());
    }

    @Override
    protected void onLoadFinished(boolean more, int count, boolean successful,
                                  List<Doulist> response, ApiError error) {
        if (successful) {
            if (more) {
                append(response);
                getListener().onLoadDoulistListFinished(getRequestCode());
                getListener().onDoulistListAppended(getRequestCode(),
                        Collections.unmodifiableList(response));
            } else {
                set(response);
                getListener().onLoadDoulistListFinished(getRequestCode());
                getListener().onDoulistListChanged(getRequestCode(),
                        Collections.unmodifiableList(get()));
            }
            for (Doulist doulist : response) {
                EventBusUtils.postAsync(new DoulistUpdatedEvent(doulist, this));
            }
        } else {
            getListener().onLoadDoulistListFinished(getRequestCode());
            getListener().onLoadDoulistListError(getRequestCode(), error);
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onDoulistUpdated(DoulistUpdatedEvent event) {

        if (event.isFromMyself(this) || isEmpty()) {
            return;
        }

        List<Doulist> doulistList = get();
        for (int i = 0, size = doulistList.size(); i < size; ++i) {
            Doulist doulist = doulistList.get(i);
            if (doulist.id == event.doulist.id) {
                doulistList.set(i, event.doulist);
                getListener().onDoulistChanged(getRequestCode(), i, doulistList.get(i));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onDoulistDeleted(DoulistDeletedEvent event) {

        if (event.isFromMyself(this) || isEmpty()) {
            return;
        }

        List<Doulist> doulistList = get();
        for (int i = 0, size = doulistList.size(); i < size; ) {
            Doulist doulist = doulistList.get(i);
            if (doulist.id == event.doulistId) {
                doulistList.remove(i);
                getListener().onDoulistRemoved(getRequestCode(), i);
                --size;
            } else {
                ++i;
            }
        }
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener {
        void onLoadDoulistListStarted(int requestCode);
        void onLoadDoulistListFinished(int requestCode);
        void onLoadDoulistListError(int requestCode, ApiError error);
        /**
         * @param newDoulistList Unmodifiable.
         */
        void onDoulistListChanged(int requestCode, List<Doulist> newDoulistList);
        /**
         * @param appendedDoulistList Unmodifiable.
         */
        void onDoulistListAppended(int requestCode, List<Doulist> appendedDoulistList);
        void onDoulistChanged(int requestCode, int position, Doulist newDoulist);
        void onDoulistRemoved(int requestCode, int position);
    }
}
