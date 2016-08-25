/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.profile.ui;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.link.UriHandler;
import me.zhanghai.android.douya.network.api.info.apiv2.UserInfo;
import me.zhanghai.android.douya.network.api.info.frodo.Review;
import me.zhanghai.android.douya.ui.FriendlyCardView;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.StringUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class ProfileReviewsLayout extends FriendlyCardView {

    private static final int REVIEW_COUNT_MAX = 3;

    @BindView(R.id.title)
    TextView mTitleText;
    @BindView(R.id.review_list)
    LinearLayout mReviewList;
    @BindView(R.id.empty)
    View mEmptyView;
    @BindView(R.id.view_more)
    TextView mViewMoreText;

    public ProfileReviewsLayout(Context context) {
        super(context);

        init();
    }

    public ProfileReviewsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public ProfileReviewsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        ViewUtils.inflateInto(R.layout.profile_reviews_layout, this);
        ButterKnife.bind(this);
    }

    public void bind(final UserInfo userInfo, List<Review> reviewList) {

        final Context context = getContext();
        OnClickListener viewMoreListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
                UriHandler.open(StringUtils.formatUs("https://www.douban.com/people/%s/reviews",
                        userInfo.getIdOrUid()), context);
                //context.startActivity(ReviewListActivity.makeIntent(userInfo, context));
            }
        };
        mTitleText.setOnClickListener(viewMoreListener);
        mViewMoreText.setOnClickListener(viewMoreListener);

        int i = 0;
        for (final Review review : reviewList) {

            if (i >= REVIEW_COUNT_MAX) {
                break;
            }

            if (i >= mReviewList.getChildCount()) {
                LayoutInflater.from(context)
                        .inflate(R.layout.profile_review_item, mReviewList);
            }
            View reviewLayout = mReviewList.getChildAt(i);
            ReviewLayoutHolder holder = (ReviewLayoutHolder) reviewLayout.getTag();
            if (holder == null) {
                holder = new ReviewLayoutHolder(reviewLayout);
                reviewLayout.setTag(holder);
                ViewUtils.setTextViewLinkClickable(holder.titleText);
            }

            String coverUrl = review.cover;
            if (TextUtils.isEmpty(coverUrl) && review.item != null && review.item.cover != null) {
                coverUrl = review.item.cover.getNormal();
            }
            if (!TextUtils.isEmpty(coverUrl)) {
                holder.coverImage.setVisibility(VISIBLE);
                ImageUtils.loadImage(holder.coverImage, coverUrl);
            } else {
                holder.coverImage.setVisibility(GONE);
            }
            holder.titleText.setText(review.title);
            holder.abstractText.setText(review.abstract_);
            reviewLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO
                    UriHandler.open(StringUtils.formatUs("https://www.douban.com/review/%d",
                            review.id), context);
                    //context.startActivity(ReviewActivity.makeIntent(review, context));
                }
            });

            ++i;
        }

        ViewUtils.setVisibleOrGone(mReviewList, i != 0);
        ViewUtils.setVisibleOrGone(mEmptyView, i == 0);

        // HACK: We don't have userInfo.reviewCount, but normally we request more than
        // REVIEW_COUNT_MAX.
        // FIXME: Fix this hack?
        //if (userInfo.reviewCount > i) {
        if (reviewList.size() > i) {
            mViewMoreText.setText(R.string.view_more);
        } else {
            mViewMoreText.setVisibility(GONE);
        }

        for (int count = mReviewList.getChildCount(); i < count; ++i) {
            ViewUtils.setVisibleOrGone(mReviewList.getChildAt(i), false);
        }
    }

    static class ReviewLayoutHolder {

        @BindView(R.id.cover)
        public ImageView coverImage;
        @BindView(R.id.title)
        public TextView titleText;
        @BindView(R.id.abstract_)
        public TextView abstractText;

        public ReviewLayoutHolder(View reviewLayout) {
            ButterKnife.bind(this, reviewLayout);
        }
    }
}
