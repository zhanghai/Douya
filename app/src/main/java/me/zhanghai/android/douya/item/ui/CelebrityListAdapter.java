/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.link.UriHandler;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleCelebrity;
import me.zhanghai.android.douya.ui.RatioFrameLayout;
import me.zhanghai.android.douya.ui.SimpleAdapter;
import me.zhanghai.android.douya.util.DrawableUtils;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class CelebrityListAdapter
        extends SimpleAdapter<SimpleCelebrity, CelebrityListAdapter.ViewHolder> {

    public CelebrityListAdapter() {
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        // Deliberately using plain hash code to identify only this instance.
        return getItem(position).id;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(ViewUtils.inflate(R.layout.item_celebrity_item, parent));
        holder.celebrityLayout.setRatio(2, 3);
        ViewCompat.setBackground(holder.scrimView, DrawableUtils.makeScrimDrawable());
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        SimpleCelebrity celebrity = getItem(position);
        ImageUtils.loadImage(holder.avatarImage, celebrity.avatar);
        holder.nameText.setText(celebrity.name);
        if (celebrity.isDirector) {
            holder.descriptionText.setText(R.string.item_celebrity_director);
        } else {
            holder.descriptionText.setText(celebrity.character);
        }
        holder.celebrityLayout.setOnClickListener(view -> {
            // TODO
            UriHandler.open(celebrity.url, view.getContext());
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.celebrity)
        public RatioFrameLayout celebrityLayout;
        @BindView(R.id.avatar)
        public ImageView avatarImage;
        @BindView(R.id.scrim)
        public View scrimView;
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
