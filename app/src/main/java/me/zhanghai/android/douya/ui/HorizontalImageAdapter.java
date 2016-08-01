/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Space;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.info.apiv2.Image;
import me.zhanghai.android.douya.util.ViewUtils;

public class HorizontalImageAdapter
        extends SimpleAdapter<Image, HorizontalImageAdapter.ViewHolder> {

    private OnImageClickListener mOnImageClickListener;

    public HorizontalImageAdapter() {
        setHasStableIds(true);
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        mOnImageClickListener = listener;
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
        holder.imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnImageClickListener != null) {
                    mOnImageClickListener.onImageClick(holder.getAdapterPosition());
                }
            }
        });
        // FIXME: This won't work properly if items are changed.
        ViewUtils.setVisibleOrGone(holder.dividerSpace, position != getItemCount() - 1);
    }

    public interface OnImageClickListener {
        void onImageClick(int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image)
        public ImageLayout imageLayout;
        @BindView(R.id.divider)
        public Space dividerSpace;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
