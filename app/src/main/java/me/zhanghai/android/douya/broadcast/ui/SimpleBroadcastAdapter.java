/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.broadcast.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.info.frodo.Broadcast;
import me.zhanghai.android.douya.ui.SimpleAdapter;
import me.zhanghai.android.douya.ui.TimeTextView;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.RecyclerViewUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class SimpleBroadcastAdapter
        extends SimpleAdapter<Broadcast, SimpleBroadcastAdapter.ViewHolder> {

    public SimpleBroadcastAdapter() {
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        //noinspection deprecation
        return getItem(position).id;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ViewUtils.inflate(R.layout.simple_broadcast_item, parent));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Context context = RecyclerViewUtils.getContext(holder);
        Broadcast broadcast = getItem(position);
        holder.itemView.setOnClickListener(view -> {
            if (broadcast.isSimpleRebroadcast()) {
                return;
            }
            // TODO: Can we pass the broadcast? But rebroadcastedBroadcast and parentBroadcast will
            // be missing.
            context.startActivity(BroadcastActivity.makeIntent(broadcast.id, context));
        });
        ImageUtils.loadAvatar(holder.avatarImage, broadcast.author.avatar);
        holder.nameText.setText(broadcast.author.name);
        holder.timeText.setDoubanTime(broadcast.createdAt);
        holder.textText.setText(broadcast.getRebroadcastText(context));
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
