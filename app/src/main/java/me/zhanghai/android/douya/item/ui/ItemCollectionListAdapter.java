/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.item.content.VoteItemCollectionManager;
import me.zhanghai.android.douya.network.api.info.frodo.CollectableItem;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleItemCollection;
import me.zhanghai.android.douya.profile.ui.ProfileActivity;
import me.zhanghai.android.douya.ui.SimpleAdapter;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.TimeUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class ItemCollectionListAdapter
        extends SimpleAdapter<SimpleItemCollection, ItemCollectionListAdapter.ViewHolder> {

    private Listener mListener;

    private CollectableItem.Type mItemType;
    private long mItemId;

    public ItemCollectionListAdapter(Listener listener) {
        mListener = listener;
        setHasStableIds(true);
    }

    public void setItem(CollectableItem item) {
        mItemType = item.getType();
        mItemId = item.id;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ViewUtils.inflate(R.layout.item_collection_item, parent));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SimpleItemCollection itemCollection = getItem(position);
        ImageUtils.loadAvatar(holder.avatarImage, itemCollection.user.avatar);
        holder.avatarImage.setOnClickListener(view -> {
            Context context = view.getContext();
            context.startActivity(ProfileActivity.makeIntent(itemCollection.user, context));
        });
        holder.nameText.setText(itemCollection.user.name);
        boolean hasRating = itemCollection.rating != null;
        ViewUtils.setVisibleOrGone(holder.ratingBar, hasRating);
        if (hasRating) {
            holder.ratingBar.setRating(itemCollection.rating.getRatingBarRating());
        }
        holder.dateText.setText(TimeUtils.formatDate(TimeUtils.parseDoubanDateTime(
                itemCollection.createTime), holder.dateText.getContext()));
        String voteCount = itemCollection.voteCount > 0 ?
                holder.voteCountText.getContext().getString(
                        R.string.item_collection_vote_count_format, itemCollection.voteCount)
                : null;
        holder.voteCountText.setText(voteCount);
        holder.voteLayout.setActivated(itemCollection.isVoted);
        holder.voteLayout.setEnabled(!VoteItemCollectionManager.getInstance().isWriting(
                itemCollection.id));
        holder.voteLayout.setOnClickListener(view -> VoteItemCollectionManager.getInstance().write(
                mItemType, mItemId, itemCollection, view.getContext()));
        holder.commentText.setText(itemCollection.comment);
        holder.itemView.setOnLongClickListener(view -> {
            mListener.copyText(itemCollection.comment);
            return true;
        });
    }

    public interface Listener {
        void copyText(String text);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.avatar)
        public ImageView avatarImage;
        @BindView(R.id.name)
        public TextView nameText;
        @BindView(R.id.rating)
        public RatingBar ratingBar;
        @BindView(R.id.date)
        public TextView dateText;
        @BindView(R.id.vote_layout)
        public ViewGroup voteLayout;
        @BindView(R.id.vote_count)
        public TextView voteCountText;
        @BindView(R.id.menu)
        public ImageButton menuButton;
        @BindView(R.id.comment)
        public TextView commentText;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
