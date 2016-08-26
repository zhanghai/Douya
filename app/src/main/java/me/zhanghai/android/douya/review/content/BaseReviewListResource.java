/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.review.content;

import android.support.annotation.Keep;

import com.android.volley.VolleyError;

import java.util.Collections;
import java.util.List;

import me.zhanghai.android.douya.content.ResourceFragment;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.eventbus.ReviewDeletedEvent;
import me.zhanghai.android.douya.eventbus.ReviewUpdatedEvent;
import me.zhanghai.android.douya.network.RequestFragment;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.info.frodo.Review;
import me.zhanghai.android.douya.network.api.info.frodo.ReviewList;

public abstract class BaseReviewListResource extends ResourceFragment
        implements RequestFragment.Listener<ReviewList, BaseReviewListResource.State> {

    private static final int DEFAULT_COUNT_PER_LOAD = 20;

    private List<Review> mReviewList;

    private boolean mCanLoadMore = true;
    private boolean mLoading;
    private boolean mLoadingMore;

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
        ApiRequest<ReviewList> request = onCreateRequest(start, count);
        State state = new State(loadMore, count);
        RequestFragment.startRequest(request, state, this);
    }

    public void load(boolean loadMore) {
        load(loadMore, DEFAULT_COUNT_PER_LOAD);
    }

    protected abstract ApiRequest<ReviewList> onCreateRequest(Integer start, Integer count);

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
