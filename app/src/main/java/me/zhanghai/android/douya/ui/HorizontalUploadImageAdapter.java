/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.ViewUtils;

public class HorizontalUploadImageAdapter
        extends ClickableSimpleAdapter<Uri, HorizontalUploadImageAdapter.ViewHolder> {

    private OnRemoveImageListener mOnRemoveImageListener;

    public HorizontalUploadImageAdapter() {
        setHasStableIds(true);
    }

    public void setOnRemoveImageListener(OnRemoveImageListener listener) {
        mOnRemoveImageListener = listener;
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UploadImageLayout itemView = (UploadImageLayout) ViewUtils.inflate(
                R.layout.horizontal_upload_image_item, parent);
        itemView.setInImageList(true);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.uploadImageLayout.loadImage(getItem(position));
        holder.uploadImageLayout.setRemoveButtonOnClickListener(view -> {
            if (mOnRemoveImageListener != null) {
                mOnRemoveImageListener.onRemoveImage(holder.getAdapterPosition());
            }
        });
    }

    public interface OnRemoveImageListener {

        void onRemoveImage(int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image)
        public UploadImageLayout uploadImageLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
