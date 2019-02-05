/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.link.UriHandler;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleReview;
import me.zhanghai.android.douya.ui.SimpleAdapter;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class ReviewListAdapter extends SimpleAdapter<SimpleReview, ReviewListAdapter.ViewHolder> {

    public ReviewListAdapter() {
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ViewUtils.inflate(R.layout.item_review_item, parent));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SimpleReview review = getItem(position);
        ImageUtils.loadAvatar(holder.avatarImage, review.author.avatar);
        holder.titleText.setText(review.title);
        holder.nameText.setText(review.author.name);
        boolean hasRating = review.rating != null;
        ViewUtils.setVisibleOrGone(holder.ratingBar, hasRating);
        if (hasRating) {
            holder.ratingBar.setRating(review.rating.getRatingBarRating());
        }
        String usefulness = holder.usefulnessText.getContext().getString(
                R.string.item_review_usefulness_format, review.usefulCount,
                review.usefulCount + review.uselessCount);
        holder.usefulnessText.setText(usefulness);
        holder.abstractText.setText(review.abstract_);
        holder.itemView.setOnClickListener(view -> {
            // TODO
            UriHandler.open(review.url, view.getContext());
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.avatar)
        public ImageView avatarImage;
        @BindView(R.id.title)
        public TextView titleText;
        @BindView(R.id.name)
        public TextView nameText;
        @BindView(R.id.rating)
        public RatingBar ratingBar;
        @BindView(R.id.usefulness)
        public TextView usefulnessText;
        @BindView(R.id.abstract_)
        public TextView abstractText;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
