/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.user.content;

import com.android.volley.VolleyError;

import java.util.Collections;
import java.util.List;

import me.zhanghai.android.douya.content.ResourceFragment;
import me.zhanghai.android.douya.network.RequestFragment;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.info.User;

public abstract class UserListResource extends ResourceFragment
        implements RequestFragment.Listener<List<User>, UserListResource.State> {

    private static final int DEFAULT_COUNT_PER_LOAD = 20;

    private List<User> mUserList;

    private boolean mCanLoadMore = true;
    private boolean mLoading;

    /**
     * @return Unmodifiable user list, or {@code null}.
     */
    public List<User> get() {
        return mUserList != null ? Collections.unmodifiableList(mUserList) : null;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mUserList == null || (mUserList.isEmpty() && mCanLoadMore)) {
            load(false);
        }
    }

    public void load(boolean loadMore, int count) {

        if (mLoading || (loadMore && !mCanLoadMore)) {
            return;
        }

        mLoading = true;
        getListener().onLoadUserList(getRequestCode(), loadMore);

        Integer start = loadMore ? (mUserList != null ? mUserList.size() : 0) : null;
        ApiRequest<List<User>> request = onCreateRequest(start, count);
        State state = new State(loadMore, count);
        RequestFragment.startRequest(request, state, this);
    }

    public void load(boolean loadMore) {
        load(loadMore, DEFAULT_COUNT_PER_LOAD);
    }

    protected abstract ApiRequest<List<User>> onCreateRequest(Integer start, Integer count);

    @Override
    public void onVolleyResponse(int requestCode, final boolean successful, final List<User> result,
                                 final VolleyError error, final State requestState) {
        postOnResumed(new Runnable() {
            @Override
            public void run() {
                onLoadComplete(successful, result, error, requestState.loadMore,
                        requestState.count);
            }
        });
    }

    private void onLoadComplete(boolean successful, List<User> userList, VolleyError error,
                                boolean loadMore, int count) {

        mLoading = false;
        getListener().onLoadUserListComplete(getRequestCode(), loadMore);

        if (successful) {
            mCanLoadMore = userList.size() == count;
            if (loadMore) {
                mUserList.addAll(userList);
                getListener().onUserListAppended(getRequestCode(),
                        Collections.unmodifiableList(userList));
            } else {
                mUserList = userList;
                getListener().onUserListChanged(getRequestCode(),
                        Collections.unmodifiableList(userList));
            }
        } else {
            getListener().onLoadUserListError(getRequestCode(), error);
        }
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    static class State {

        public boolean loadMore;
        public int count;

        public State(boolean loadMore, int count) {
            this.loadMore = loadMore;
            this.count = count;
        }
    }

    public interface Listener {
        void onLoadUserList(int requestCode, boolean loadMore);
        void onLoadUserListComplete(int requestCode, boolean loadMore);
        /**
         * @param newUserList Unmodifiable.
         */
        void onUserListChanged(int requestCode, List<User> newUserList);
        /**
         * @param appendedUserList Unmodifiable.
         */
        void onUserListAppended(int requestCode, List<User> appendedUserList);
        void onLoadUserListError(int requestCode, VolleyError error);
    }
}
