/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.info.frodo.RebroadcastItem;
import me.zhanghai.android.douya.profile.ui.ProfileActivity;
import me.zhanghai.android.douya.ui.SimpleAdapter;
import me.zhanghai.android.douya.ui.TimeTextView;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class RebroadcastItemAdapter
        extends SimpleAdapter<RebroadcastItem, RebroadcastItemAdapter.ViewHolder> {

    public RebroadcastItemAdapter() {
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).createTime.hashCode();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(ViewUtils.inflate(R.layout.rebroadcast_item, parent));
        ViewUtils.setTextViewLinkClickable(holder.textText);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RebroadcastItem rebroadcastItem = getItem(position);
        holder.itemView.setOnClickListener(view -> {
            if (!rebroadcastItem.hasBroadcast()) {
                return;
            }
            Context context = view.getContext();
            context.startActivity(BroadcastActivity.makeIntent(rebroadcastItem.getBroadcastId(),
                    context));
        });
        ImageUtils.loadAvatar(holder.avatarImage, rebroadcastItem.author.avatar);
        holder.avatarImage.setOnClickListener(view -> {
            Context context = view.getContext();
            context.startActivity(ProfileActivity.makeIntent(rebroadcastItem.author, context));
        });
        holder.nameText.setText(rebroadcastItem.author.name);
        holder.timeText.setDoubanTime(rebroadcastItem.createTime);
        holder.textText.setText(rebroadcastItem.getText(holder.textText.getContext()));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.avatar)
        public ImageView avatarImage;
        @BindView(R.id.name)
        public TextView nameText;
        @BindView(R.id.time)
        public TimeTextView timeText;
        @BindView(R.id.text)
        public TextView textText;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
