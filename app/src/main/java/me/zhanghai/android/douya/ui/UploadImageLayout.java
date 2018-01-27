/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import butterknife.BindView;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.UriUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class UploadImageLayout extends ImageLayout {

    @BindView(R.id.uploadimagelayout_remove)
    ImageButton mRemoveButton;
    @BindView(R.id.uploadimagelayout_progress_layout)
    ViewGroup mProgressLayout;
    @BindView(R.id.uploadimagelayout_progress)
    ProgressBar mProgress;

    public UploadImageLayout(Context context) {
        super(context);

        init();
    }

    public UploadImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public UploadImageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public UploadImageLayout(Context context, AttributeSet attrs, int defStyleAttr,
                             int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    @Override
    protected void onInflateChildren() {
        super.onInflateChildren();

        ViewUtils.inflateInto(R.layout.upload_image_layout, this);
    }

    private void init() {
        mImageView.setAdjustViewBounds(true);
        // Need to reset scale type from ImageView.setAdjustViewBounds(true).
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    /**
     * @deprecated Use {@link #loadImage(Uri)} instead.
     */
    @Override
    public void loadImage(SizedImageItem image) {
        throw new UnsupportedOperationException("Use loadImage(Uri) instead");
    }

    public void loadImage(Uri imageUri) {
        ImageUtils.loadImage(mImageView, imageUri);
        String type = UriUtils.getType(imageUri, getContext());
        boolean isGif = TextUtils.equals(type, "image/gif");
        ViewUtils.setVisibleOrGone(mGifImage, isGif);
    }
}
