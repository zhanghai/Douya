/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.diary.content;

import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.android.volley.VolleyError;

import java.util.Collections;
import java.util.List;

import me.zhanghai.android.douya.content.ResourceFragment;
import me.zhanghai.android.douya.eventbus.DiaryDeletedEvent;
import me.zhanghai.android.douya.eventbus.DiaryUpdatedEvent;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.network.RequestFragment;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiRequests;
import me.zhanghai.android.douya.network.api.info.frodo.Diary;
import me.zhanghai.android.douya.network.api.info.frodo.DiaryList;
import me.zhanghai.android.douya.util.FragmentUtils;

public class UserDiaryListResource extends ResourceFragment
        implements RequestFragment.Listener<DiaryList, UserDiaryListResource.State> {

    private static final int DEFAULT_COUNT_PER_LOAD = 20;

    private static final String KEY_PREFIX = UserDiaryListResource.class.getName() + '.';

    private static final String EXTRA_USER_ID_OR_UID = KEY_PREFIX + "user_id_or_uid";

    private String mUserIdOrUid;

    private List<Diary> mDiaryList;

    private boolean mCanLoadMore = true;
    private boolean mLoading;
    private boolean mLoadingMore;

    private static final String FRAGMENT_TAG_DEFAULT = UserDiaryListResource.class.getName();

    private static UserDiaryListResource newInstance(String userIdOrUid) {
        //noinspection deprecation
        UserDiaryListResource resource = new UserDiaryListResource();
        resource.setArguments(userIdOrUid);
        return resource;
    }

    public static UserDiaryListResource attachTo(String userIdOrUid, FragmentActivity activity,
                                                 String tag, int requestCode) {
        return attachTo(userIdOrUid, activity, tag, true, null, requestCode);
    }

    public static UserDiaryListResource attachTo(String userIdOrUid, FragmentActivity activity) {
        return attachTo(userIdOrUid, activity, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    public static UserDiaryListResource attachTo(String userIdOrUid, Fragment fragment, String tag,
                                                 int requestCode) {
        return attachTo(userIdOrUid, fragment.getActivity(), tag, false, fragment, requestCode);
    }

    public static UserDiaryListResource attachTo(String userIdOrUid, Fragment fragment) {
        return attachTo(userIdOrUid, fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    private static UserDiaryListResource attachTo(String userIdOrUid, FragmentActivity activity,
                                                  String tag, boolean targetAtActivity,
                                                  Fragment targetFragment, int requestCode) {
        UserDiaryListResource resource = FragmentUtils.findByTag(activity, tag);
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
    public UserDiaryListResource() {}

    private void setArguments(String userIdOrUid) {
        FragmentUtils.ensureArguments(this)
                .putString(EXTRA_USER_ID_OR_UID, userIdOrUid);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserIdOrUid = getArguments().getString(EXTRA_USER_ID_OR_UID);
    }

    /**
     * @return Unmodifiable diary list, or {@code null}.
     */
    public List<Diary> get() {
        return mDiaryList != null ? Collections.unmodifiableList(mDiaryList) : null;
    }

    public boolean has() {
        return mDiaryList != null;
    }

    public boolean isEmpty() {
        return mDiaryList == null || mDiaryList.isEmpty();
    }

    public boolean isLoading() {
        return mLoading;
    }

    public boolean isLoadingMore() {
        return mLoadingMore;
    }

    @Override
    public void onStart() {
        super.onStart();

        EventBusUtils.register(this);

        if (mDiaryList == null || (mDiaryList.isEmpty() && mCanLoadMore)) {
            load(false);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBusUtils.unregister(this);
    }

    public void load(boolean loadMore, int count) {

        if (mLoading || (loadMore && !mCanLoadMore)) {
            return;
        }

        mLoading = true;
        mLoadingMore = loadMore;
        getListener().onLoadDiaryListStarted(getRequestCode());

        Integer start = loadMore ? (mDiaryList != null ? mDiaryList.size() : 0) : null;
        ApiRequest<DiaryList> request = ApiRequests.newDiaryListRequest(mUserIdOrUid, start, count);
        State state = new State(loadMore, count);
        RequestFragment.startRequest(request, state, this);
    }

    public void load(boolean loadMore) {
        load(loadMore, DEFAULT_COUNT_PER_LOAD);
    }

    @Override
    public void onVolleyResponse(int requestCode, final boolean successful,
                                 final DiaryList result, final VolleyError error,
                                 final State requestState) {
        postOnResumed(new Runnable() {
            @Override
            public void run() {
                onLoadFinished(successful, result != null ? result.diaries : null, error,
                        requestState.loadMore, requestState.count);
            }
        });
    }

    private void onLoadFinished(boolean successful, List<Diary> diaryList, VolleyError error,
                                boolean loadMore, int count) {

        mLoading = false;
        mLoadingMore = false;
        getListener().onLoadDiaryListFinished(getRequestCode());

        if (successful) {
            mCanLoadMore = diaryList.size() == count;
            if (loadMore) {
                mDiaryList.addAll(diaryList);
                getListener().onDiaryListAppended(getRequestCode(),
                        Collections.unmodifiableList(diaryList));
                for (Diary diary : diaryList) {
                    EventBusUtils.postAsync(new DiaryUpdatedEvent(diary, this));
                }
            } else {
                mDiaryList = diaryList;
                getListener().onDiaryListChanged(getRequestCode(),
                        Collections.unmodifiableList(diaryList));
            }
        } else {
            getListener().onLoadDiaryListError(getRequestCode(), error);
        }
    }

    @Keep
    public void onEventMainThread(DiaryUpdatedEvent event) {

        if (event.isFromMyself(this) || mDiaryList == null) {
            return;
        }

        for (int i = 0, size = mDiaryList.size(); i < size; ++i) {
            Diary diary = mDiaryList.get(i);
            if (diary.id == event.diary.id) {
                mDiaryList.set(i, event.diary);
                getListener().onDiaryChanged(getRequestCode(), i, mDiaryList.get(i));
            }
        }
    }

    @Keep
    public void onEventMainThread(DiaryDeletedEvent event) {

        if (event.isFromMyself(this) || mDiaryList == null) {
            return;
        }

        for (int i = 0, size = mDiaryList.size(); i < size; ) {
            Diary diary = mDiaryList.get(i);
            if (diary.id == event.diaryId) {
                mDiaryList.remove(i);
                getListener().onDiaryRemoved(getRequestCode(), i);
                --size;
            } else {
                ++i;
            }
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
        void onLoadDiaryListStarted(int requestCode);
        void onLoadDiaryListFinished(int requestCode);
        void onLoadDiaryListError(int requestCode, VolleyError error);
        /**
         * @param newDiaryList Unmodifiable.
         */
        void onDiaryListChanged(int requestCode, List<Diary> newDiaryList);
        /**
         * @param appendedDiaryList Unmodifiable.
         */
        void onDiaryListAppended(int requestCode, List<Diary> appendedDiaryList);
        void onDiaryChanged(int requestCode, int position, Diary newDiary);
        void onDiaryRemoved(int requestCode, int position);
    }
}
