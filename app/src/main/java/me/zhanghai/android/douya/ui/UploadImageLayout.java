/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.UriUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class UploadImageLayout extends FrameLayout {

    @BindView(R.id.uploadimagelayout_image)
    RatioImageView mImageView;
    @BindView(R.id.uploadimagelayout_gif)
    ImageView mGifImage;
    @BindView(R.id.uploadimagelayout_remove)
    ImageButton mRemoveButton;

    private boolean mInImageList;

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

    private void init() {

        setClickable(true);
        setFocusable(true);

        ViewUtils.inflateInto(R.layout.upload_image_layout, this);
        ButterKnife.bind(this);

        setInImageListInt(false);
    }

    public void setInImageList(boolean inImageList) {
        if (mInImageList != inImageList) {
            setInImageListInt(inImageList);
        }
    }

    private void setInImageListInt(boolean inImageList) {
        mInImageList = inImageList;
        mImageView.setRatio(mInImageList ? 1 : (6f / 5f));
        LayoutParams layoutParams = (LayoutParams) mImageView.getLayoutParams();
        layoutParams.width = mInImageList ? LayoutParams.WRAP_CONTENT : LayoutParams.MATCH_PARENT;
        layoutParams.height = mInImageList ? LayoutParams.MATCH_PARENT : LayoutParams.WRAP_CONTENT;
        mImageView.setLayoutParams(layoutParams);
    }

    public void loadImage(Uri imageUri) {
        ImageUtils.loadImage(mImageView, imageUri);
        String type = UriUtils.getType(imageUri, getContext());
        boolean isGif = TextUtils.equals(type, "image/gif");
        ViewUtils.setVisibleOrGone(mGifImage, isGif);
    }

    public void releaseImage() {
        mImageView.setImageDrawable(null);
    }

    public void setRemoveButtonOnClickListener(View.OnClickListener listener) {
        mRemoveButton.setOnClickListener(listener);
    }
}
