/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.link.UriHandler;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleItemForumTopic;
import me.zhanghai.android.douya.ui.SimpleAdapter;
import me.zhanghai.android.douya.ui.TimeTextView;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class ItemForumTopicListAdapter
        extends SimpleAdapter<SimpleItemForumTopic, ItemForumTopicListAdapter.ViewHolder> {

    public ItemForumTopicListAdapter() {
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ViewUtils.inflate(R.layout.item_forum_topic_item, parent));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SimpleItemForumTopic itemForumTopic = getItem(position);
        ImageUtils.loadAvatar(holder.avatarImage, itemForumTopic.author.avatar);
        holder.titleText.setText(itemForumTopic.title);
        holder.nameText.setText(itemForumTopic.author.name);
        holder.likeCountText.setText(holder.likeCountText.getContext().getString(
                R.string.item_forum_topic_like_count_format, itemForumTopic.likeCount));
        holder.commentCountText.setText(holder.commentCountText.getContext().getString(
                R.string.item_forum_topic_comment_count_format, itemForumTopic.commentCount));
        holder.updateTimeText.setDoubanTime(itemForumTopic.updateTime);
        holder.itemView.setOnClickListener(view -> {
            // TODO
            UriHandler.open(itemForumTopic.url, view.getContext());
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.avatar)
        public ImageView avatarImage;
        @BindView(R.id.title)
        public TextView titleText;
        @BindView(R.id.name)
        public TextView nameText;
        @BindView(R.id.like_count)
        public TextView likeCountText;
        @BindView(R.id.comment_count)
        public TextView commentCountText;
        @BindView(R.id.update_time)
        public TimeTextView updateTimeText;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
