/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.ViewUtils;

public class HorizontalImageAdapter
        extends ClickableSimpleAdapter<SizedImageItem, HorizontalImageAdapter.ViewHolder> {

    public HorizontalImageAdapter() {
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        // Deliberately using plain hash code to identify only this instance.
        return getItem(position).hashCode();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ViewUtils.inflate(R.layout.horizontal_image_item, parent));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.imageLayout.loadImage(getItem(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image)
        public ImageLayout imageLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
