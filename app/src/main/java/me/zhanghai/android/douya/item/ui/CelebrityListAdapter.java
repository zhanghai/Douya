/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.item.ui;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.link.UriHandler;
import me.zhanghai.android.douya.network.api.info.frodo.SimpleCelebrity;
import me.zhanghai.android.douya.ui.RatioImageView;
import me.zhanghai.android.douya.ui.SimpleAdapter;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class CelebrityListAdapter
        extends SimpleAdapter<SimpleCelebrity, CelebrityListAdapter.ViewHolder> {

    public CelebrityListAdapter() {
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(ViewUtils.inflate(R.layout.item_celebrity_item, parent));
        holder.avatarImage.setRatio(2, 3);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SimpleCelebrity celebrity = getItem(position);
        ImageUtils.loadImage(holder.avatarImage, celebrity.avatar);
        holder.nameText.setText(celebrity.name);
        if (celebrity.isDirector) {
            holder.descriptionText.setText(R.string.item_celebrity_director);
        } else {
            holder.descriptionText.setText(celebrity.character);
        }
        holder.itemView.setOnClickListener(view -> {
            // TODO
            UriHandler.open(celebrity.url, view.getContext());
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.avatar)
        public RatioImageView avatarImage;
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
