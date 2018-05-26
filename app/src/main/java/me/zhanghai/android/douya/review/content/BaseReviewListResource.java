/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.review.content;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.List;

import me.zhanghai.android.douya.content.MoreBaseListResourceFragment;
import me.zhanghai.android.douya.eventbus.EventBusUtils;
import me.zhanghai.android.douya.eventbus.ReviewDeletedEvent;
import me.zhanghai.android.douya.eventbus.ReviewUpdatedEvent;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleReview;
import me.zhanghai.android.douya.network.api.info.frodo.ReviewList;

public abstract class BaseReviewListResource
        extends MoreBaseListResourceFragment<ReviewList, SimpleReview> {

    @Override
    protected void onLoadStarted() {
        getListener().onLoadReviewListStarted(getRequestCode());
    }

    @Override
    protected void onLoadFinished(boolean more, int count, boolean successful,
                                  List<SimpleReview> response, ApiError error) {
        if (successful) {
            if (more) {
                append(response);
                getListener().onLoadReviewListFinished(getRequestCode());
                getListener().onReviewListAppended(getRequestCode(),
                        Collections.unmodifiableList(response));
            } else {
                set(response);
                getListener().onLoadReviewListFinished(getRequestCode());
                getListener().onReviewListChanged(getRequestCode(),
                        Collections.unmodifiableList(get()));
            }
            for (SimpleReview review : response) {
                EventBusUtils.postAsync(new ReviewUpdatedEvent(review, this));
            }
        } else {
            getListener().onLoadReviewListFinished(getRequestCode());
            getListener().onLoadReviewListError(getRequestCode(), error);
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onReviewUpdated(ReviewUpdatedEvent event) {

        if (event.isFromMyself(this) || isEmpty()) {
            return;
        }

        List<SimpleReview> reviewList = get();
        for (int i = 0, size = reviewList.size(); i < size; ++i) {
            SimpleReview review = reviewList.get(i);
            if (review.id == event.review.id) {
                reviewList.set(i, event.review);
                getListener().onReviewChanged(getRequestCode(), i, reviewList.get(i));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onReviewDeleted(ReviewDeletedEvent event) {

        if (event.isFromMyself(this) || isEmpty()) {
            return;
        }

        List<SimpleReview> reviewList = get();
        for (int i = 0, size = reviewList.size(); i < size; ) {
            SimpleReview review = reviewList.get(i);
            if (review.id == event.reviewId) {
                reviewList.remove(i);
                getListener().onReviewRemoved(getRequestCode(), i);
                --size;
            } else {
                ++i;
            }
        }
    }

    protected Listener getListener() {
        return (Listener) getTarget();
    }

    public interface Listener {
        void onLoadReviewListStarted(int requestCode);
        void onLoadReviewListFinished(int requestCode);
        void onLoadReviewListError(int requestCode, ApiError error);
        /**
         * @param newReviewList Unmodifiable.
         */
        void onReviewListChanged(int requestCode, List<SimpleReview> newReviewList);
        /**
         * @param appendedReviewList Unmodifiable.
         */
        void onReviewListAppended(int requestCode, List<SimpleReview> appendedReviewList);
        void onReviewChanged(int requestCode, int position, SimpleReview newReview);
        void onReviewRemoved(int requestCode, int position);
    }
}
