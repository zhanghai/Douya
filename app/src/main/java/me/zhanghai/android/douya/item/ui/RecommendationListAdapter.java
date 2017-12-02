/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.link.UriHandler;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.ui.RatioImageView;
import me.zhanghai.android.douya.ui.SimpleAdapter;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class RecommendationListAdapter
        extends SimpleAdapter<CollectableItem, RecommendationListAdapter.ViewHolder> {

    public RecommendationListAdapter() {
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(ViewUtils.inflate(R.layout.item_recommendation_item,
                parent));
        holder.coverImage.setRatio(27, 40);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CollectableItem item = getItem(position);
        ImageUtils.loadImage(holder.coverImage, item.cover);
        holder.titleText.setText(item.title);
        boolean hasRating = item.rating.hasRating();
        if (hasRating) {
            holder.ratingText.setText(item.rating.getRatingString(holder.ratingText.getContext()));
        } else {
            holder.ratingText.setText(item.getRatingUnavailableReason(
                    holder.ratingText.getContext()));
        }
        ViewUtils.setVisibleOrGone(holder.ratingStarText, hasRating);
        holder.itemView.setOnClickListener(view -> {
            // TODO
            UriHandler.open(item.url, view.getContext());
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cover)
        public RatioImageView coverImage;
        @BindView(R.id.title)
        public TextView titleText;
        @BindView(R.id.rating)
        public TextView ratingText;
        @BindView(R.id.rating_star)
        public TextView ratingStarText;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
