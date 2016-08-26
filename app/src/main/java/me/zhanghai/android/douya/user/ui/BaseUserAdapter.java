/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.user.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.info.apiv2.User;
import me.zhanghai.android.douya.profile.ui.ProfileActivity;
import me.zhanghai.android.douya.ui.SimpleAdapter;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.RecyclerViewUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public abstract class BaseUserAdapter extends SimpleAdapter<User, BaseUserAdapter.ViewHolder> {

    public BaseUserAdapter() {
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        //noinspection deprecation
        return getItem(position).id;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ViewUtils.inflate(getLayoutResource(), parent));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Context context = RecyclerViewUtils.getContext(holder);
        final User user = getItem(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(ProfileActivity.makeIntent(user, context));
            }
        });
        ImageUtils.loadAvatar(holder.avatarImage, user.avatar);
        holder.nameText.setText(user.name);
        //noinspection deprecation
        holder.descriptionText.setText(user.uid);
    }

    abstract protected int getLayoutResource();

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.avatar)
        public ImageView avatarImage;
        @BindView(R.id.name)
        public TextView nameText;
        @BindView(R.id.description)
        public TextView descriptionText;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
