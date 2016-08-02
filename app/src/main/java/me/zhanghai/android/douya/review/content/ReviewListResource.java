/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.review.content;

import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.android.volley.VolleyError;

import java.util.Collections;
import java.util.List;

import me.zhanghai.android.douya.content.ResourceFragment;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.eventbus.ReviewDeletedEvent;
import me.zhanghai.android.douya.eventbus.ReviewUpdatedEvent;
import me.zhanghai.android.douya.network.RequestFragment;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.ApiRequests;
import me.zhanghai.android.douya.network.api.info.frodo.Review;
import me.zhanghai.android.douya.network.api.info.frodo.ReviewList;
import me.zhanghai.android.douya.util.FragmentUtils;

public class ReviewListResource extends ResourceFragment
        implements RequestFragment.Listener<ReviewList, ReviewListResource.State> {

    private static final int DEFAULT_COUNT_PER_LOAD = 20;

    // Not static because we are to be subclassed.
    private final String KEY_PREFIX = getClass().getName() + '.';

    public final String EXTRA_USER_ID_OR_UID = KEY_PREFIX + "user_id_or_uid";

    private String mUserIdOrUid;

    private List<Review> mReviewList;

    private boolean mCanLoadMore = true;
    private boolean mLoading;
    private boolean mLoadingMore;

    private static final String FRAGMENT_TAG_DEFAULT = ReviewListResource.class.getName();

    private static ReviewListResource newInstance(String userIdOrUid) {
        //noinspection deprecation
        ReviewListResource resource = new ReviewListResource();
        resource.setArguments(userIdOrUid);
        return resource;
    }

    public static ReviewListResource attachTo(String userIdOrUid, FragmentActivity activity,
                                              String tag, int requestCode) {
        return attachTo(userIdOrUid, activity, tag, true, null, requestCode);
    }

    public static ReviewListResource attachTo(String userIdOrUid, FragmentActivity activity) {
        return attachTo(userIdOrUid, activity, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    public static ReviewListResource attachTo(String userIdOrUid, Fragment fragment, String tag,
                                              int requestCode) {
        return attachTo(userIdOrUid, fragment.getActivity(), tag, false, fragment, requestCode);
    }

    public static ReviewListResource attachTo(String userIdOrUid, Fragment fragment) {
        return attachTo(userIdOrUid, fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    private static ReviewListResource attachTo(String userIdOrUid, FragmentActivity activity,
                                               String tag, boolean targetAtActivity,
                                               Fragment targetFragment, int requestCode) {
        ReviewListResource resource = FragmentUtils.findByTag(activity, tag);
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
    public ReviewListResource() {}

    private void setArguments(String userIdOrUid) {
        FragmentUtils.ensureArguments(this).putString(EXTRA_USER_ID_OR_UID, userIdOrUid);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserIdOrUid = getArguments().getString(EXTRA_USER_ID_OR_UID);
    }

    /**
     * @return Unmodifiable review list, or {@code null}.
     */
    public List<Review> get() {
        return mReviewList != null ? Collections.unmodifiableList(mReviewList) : null;
    }

    public boolean has() {
        return mReviewList != null;
    }

    public boolean isEmpty() {
        return mReviewList == null || mReviewList.isEmpty();
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

        if (mReviewList == null || (mReviewList.isEmpty() && mCanLoadMore)) {
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
        getListener().onLoadReviewListStarted(getRequestCode());

        Integer start = loadMore ? (mReviewList != null ? mReviewList.size() : 0) : null;
        ApiRequest<ReviewList> request = ApiRequests.newReviewListRequest(mUserIdOrUid, start, count,
                getActivity());
        State state = new State(loadMore, count);
        RequestFragment.startRequest(request, state, this);
    }

    public void load(boolean loadMore) {
        load(loadMore, DEFAULT_COUNT_PER_LOAD);
    }

    @Override
    public void onVolleyResponse(int requestCode, final boolean successful,
                                 final ReviewList result, final VolleyError error,
                                 final State requestState) {
        postOnResumed(new Runnable() {
            @Override
            public void run() {
                onLoadFinished(successful, result != null ? result.reviews : null, error,
                        requestState.loadMore, requestState.count);
            }
        });
    }

    private void onLoadFinished(boolean successful, List<Review> reviewList, VolleyError error,
                                boolean loadMore, int count) {

        mLoading = false;
        mLoadingMore = false;
        getListener().onLoadReviewListFinished(getRequestCode());

        if (successful) {
            mCanLoadMore = reviewList.size() == count;
            if (loadMore) {
                mReviewList.addAll(reviewList);
                getListener().onReviewListAppended(getRequestCode(),
                        Collections.unmodifiableList(reviewList));
                for (Review review : reviewList) {
                    EventBusUtils.postAsync(new ReviewUpdatedEvent(review, this));
                }
            } else {
                mReviewList = reviewList;
                getListener().onReviewListChanged(getRequestCode(),
                        Collections.unmodifiableList(reviewList));
            }
        } else {
            getListener().onLoadReviewListError(getRequestCode(), error);
        }
    }

    @Keep
    public void onEventMainThread(ReviewUpdatedEvent event) {

        if (event.isFromMyself(this) || mReviewList == null) {
            return;
        }

        for (int i = 0, size = mReviewList.size(); i < size; ++i) {
            Review review = mReviewList.get(i);
            if (review.id == event.review.id) {
                mReviewList.set(i, event.review);
                getListener().onReviewChanged(getRequestCode(), i, mReviewList.get(i));
            }
        }
    }

    @Keep
    public void onEventMainThread(ReviewDeletedEvent event) {

        if (event.isFromMyself(this) || mReviewList == null) {
            return;
        }

        for (int i = 0, size = mReviewList.size(); i < size; ) {
            Review review = mReviewList.get(i);
            if (review.id == event.reviewId) {
                mReviewList.remove(i);
                getListener().onReviewRemoved(getRequestCode(), i);
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
        void onLoadReviewListStarted(int requestCode);
        void onLoadReviewListFinished(int requestCode);
        void onLoadReviewListError(int requestCode, VolleyError error);
        /**
         * @param newReviewList Unmodifiable.
         */
        void onReviewListChanged(int requestCode, List<Review> newReviewList);
        /**
         * @param appendedReviewList Unmodifiable.
         */
        void onReviewListAppended(int requestCode, List<Review> appendedReviewList);
        void onReviewChanged(int requestCode, int position, Review newReview);
        void onReviewRemoved(int requestCode, int position);
    }
}
