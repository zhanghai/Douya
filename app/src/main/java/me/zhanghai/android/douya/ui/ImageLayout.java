/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.info.apiv2.Image;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class ImageLayout extends FrameLayout {

    public static final int FILL_ORIENTATION_HORIZONTAL = 0;
    public static final int FILL_ORIENTATION_VERTICAL = 1;

    @BindView(R.id.imagelayout_image)
    RatioImageView mImageView;
    @BindView(R.id.imagelayout_gif)
    ImageView mGifImage;

    public ImageLayout(Context context) {
        super(context);

        init(null, 0, 0);
    }

    public ImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(attrs, 0, 0);
    }

    public ImageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ImageLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(attrs, defStyleAttr, defStyleRes);
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {

        setClickable(true);
        setFocusable(true);

        ViewUtils.inflateInto(R.layout.image_layout, this);
        ButterKnife.bind(this);

        TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs,
                R.styleable.ImageLayout, defStyleAttr, defStyleRes);
        int fillOrientation = a.getInt(R.styleable.ImageLayout_fillOrientation,
                FILL_ORIENTATION_HORIZONTAL);
        a.recycle();

        LayoutParams layoutParams = (LayoutParams) mImageView.getLayoutParams();
        layoutParams.width = fillOrientation == FILL_ORIENTATION_HORIZONTAL ?
                LayoutParams.MATCH_PARENT : LayoutParams.WRAP_CONTENT;
        layoutParams.height = fillOrientation == FILL_ORIENTATION_HORIZONTAL ?
                LayoutParams.WRAP_CONTENT : LayoutParams.MATCH_PARENT;
        mImageView.setLayoutParams(layoutParams);
    }

    public void loadImage(Image image) {
        ImageUtils.loadImage(mImageView, image);
        ViewUtils.setVisibleOrGone(mGifImage, image.animated);
    }

    public void releaseImage() {
        mImageView.setImageDrawable(null);
    }
}
