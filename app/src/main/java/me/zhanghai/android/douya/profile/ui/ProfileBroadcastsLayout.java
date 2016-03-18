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

import butterknife.Bind;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.broadcast.ui.BroadcastActivity;
import me.zhanghai.android.douya.network.api.info.Broadcast;
import me.zhanghai.android.douya.network.api.info.Image;
import me.zhanghai.android.douya.network.api.info.Photo;
import me.zhanghai.android.douya.ui.FriendlyCardView;
import me.zhanghai.android.douya.ui.TimeActionTextView;
import me.zhanghai.android.douya.util.ContentStateLayout;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class ProfileBroadcastsLayout extends FriendlyCardView {

    private static final int BROADCAST_COUNT_MAX = 3;

    @Bind(R.id.title)
    TextView mTitleText;
    @Bind(R.id.contentState)
    ContentStateLayout mContentStateLayout;
    @Bind(R.id.broadcast_list)
    LinearLayout mBroadcastList;
    @Bind(R.id.view_all)
    TextView mViewAllText;

    public ProfileBroadcastsLayout(Context context) {
        super(context);

        init();
    }

    public ProfileBroadcastsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public ProfileBroadcastsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        inflate(getContext(), R.layout.profile_broadcasts_layout, this);
        ButterKnife.bind(this);
    }

    public void setLoading() {
        mContentStateLayout.setLoading();
    }

    public void bind(final String userIdOrUid, List<Broadcast> broadcastList) {

        final Context context = getContext();
        View.OnClickListener viewAllListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                //context.startActivity(BroadcastListActivity.makeIntent(userIdOrUid));
            }
        };
        mTitleText.setOnClickListener(viewAllListener);
        mViewAllText.setOnClickListener(viewAllListener);

        int i = 0;
        for (final Broadcast broadcast : broadcastList) {

            if (i >= BROADCAST_COUNT_MAX) {
                break;
            }

            if (TextUtils.isEmpty(broadcast.text) || broadcast.isRebroadcasted()) {
                continue;
            }

            if (i >= mBroadcastList.getChildCount()) {
                LayoutInflater.from(context)
                        .inflate(R.layout.profile_broadcast_item, mBroadcastList);
            }
            View broadcastLayout = mBroadcastList.getChildAt(i);
            BroadcastLayoutHolder holder = (BroadcastLayoutHolder) broadcastLayout.getTag();
            if (holder == null) {
                holder = new BroadcastLayoutHolder(broadcastLayout);
                broadcastLayout.setTag(holder);
            }

            // HACK: Should not change on rebind.
            if (holder.boundBroadcastId != broadcast.id) {
                String imageUrl = null;
                if (broadcast.attachment != null) {
                    imageUrl = broadcast.attachment.image;
                }
                if (TextUtils.isEmpty(imageUrl)) {
                    List<Image> images = broadcast.images.size() > 0 ? broadcast.images
                            : Photo.toImageList(broadcast.photos);
                    if (images.size() > 0){
                        imageUrl = images.get(0).medium;
                    }
                }
                ViewUtils.setVisibleOrGone(holder.image, !TextUtils.isEmpty(imageUrl));
                ImageUtils.loadImage(holder.image, imageUrl, context);
                holder.textText.setText(broadcast.getTextWithEntities(context));
                holder.timeActionText.setDoubanTimeAndAction(broadcast.createdAt, broadcast.action);
                broadcastLayout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.startActivity(BroadcastActivity.makeIntent(broadcast, context));
                    }
                });
                holder.boundBroadcastId = broadcast.id;
            }

            ++i;
        }
        mContentStateLayout.setLoaded(i != 0);
        for (int count = mBroadcastList.getChildCount(); i < count; ++i) {
            ViewUtils.setVisibleOrGone(mBroadcastList.getChildAt(i), false);
        }
    }

    public void setError() {
        mContentStateLayout.setError();
    }

    static class BroadcastLayoutHolder {

        @Bind(R.id.image)
        public ImageView image;
        @Bind(R.id.text)
        public TextView textText;
        @Bind(R.id.time_action)
        public TimeActionTextView timeActionText;
        public long boundBroadcastId;

        public BroadcastLayoutHolder(View broadcastLayout) {
            ButterKnife.bind(this, broadcastLayout);
        }
    }
}
