/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.review.content;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;

import me.zhanghai.android.douya.content.MoreRawListResourceFragment;
import me.zhanghai.android.douya.content.ResourceFragment;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.eventbus.ReviewDeletedEvent;
import me.zhanghai.android.douya.eventbus.ReviewUpdatedEvent;
import me.zhanghai.android.douya.network.RequestFragment;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.ApiRequest;
import me.zhanghai.android.douya.network.api.info.frodo.Review;
import me.zhanghai.android.douya.network.api.info.frodo.ReviewList;
import me.zhanghai.android.douya.util.LogUtils;

public abstract class BaseReviewListResource
        extends MoreRawListResourceFragment<Review, ReviewList> {

    @Override
    protected ApiRequest<ReviewList> onCreateRequest(boolean more, int count) {
        Integer start = more ? (has() ? get().size() : 0) : null;
        return onCreateRequest(start, count);
    }

    protected abstract ApiRequest<ReviewList> onCreateRequest(Integer start, Integer count);

    @Override
    protected void onLoadStarted() {
        getListener().onLoadReviewListStarted(getRequestCode());
    }

    @Override
    protected void onLoadFinished(boolean more, int count, boolean successful, ReviewList response,
                                  ApiError error) {
        onLoadFinished(more, count, successful, response != null ? response.reviews : null, error);
    }

    private void onLoadFinished(boolean more, int count, boolean successful,
                                List<Review> response, ApiError error) {
        getListener().onLoadReviewListFinished(getRequestCode());
        if (successful) {
            if (more) {
                append(response);
                getListener().onReviewListAppended(getRequestCode(),
                        Collections.unmodifiableList(response));
            } else {
                set(response);
                getListener().onReviewListChanged(getRequestCode(),
                        Collections.unmodifiableList(get()));
            }
            for (Review review : response) {
                EventBusUtils.postAsync(new ReviewUpdatedEvent(review, this));
            }
        } else {
            getListener().onLoadReviewListError(getRequestCode(), error);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReviewUpdated(ReviewUpdatedEvent event) {

        if (event.isFromMyself(this) || isEmpty()) {
            return;
        }

        List<Review> reviewList = get();
        for (int i = 0, size = reviewList.size(); i < size; ++i) {
            Review review = reviewList.get(i);
            if (review.id == event.review.id) {
                reviewList.set(i, event.review);
                getListener().onReviewChanged(getRequestCode(), i, reviewList.get(i));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReviewDeleted(ReviewDeletedEvent event) {

        if (event.isFromMyself(this) || isEmpty()) {
            return;
        }

        List<Review> reviewList = get();
        for (int i = 0, size = reviewList.size(); i < size; ) {
            Review review = reviewList.get(i);
            if (review.id == event.reviewId) {
                reviewList.remove(i);
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

    public interface Listener {
        void onLoadReviewListStarted(int requestCode);
        void onLoadReviewListFinished(int requestCode);
        void onLoadReviewListError(int requestCode, ApiError error);
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
