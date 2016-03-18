/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ContentLayout extends FrameLayout {

    @IntDef({STATE_LOADING, STATE_CONTENT, STATE_EMPTY, STATE_ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {}

    public static final int STATE_LOADING = 0;
    public static final int STATE_CONTENT = 1;
    public static final int STATE_EMPTY = 2;
    public static final int STATE_ERROR = 3;

    private View mLoadingView;
    private View mContentView;
    private View mEmptyView;
    private View mErrorView;

    public ContentLayout(Context context) {
        super(context);
    }

    public ContentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContentLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ContentLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setState(@State int state) {
        ViewUtils.fadeToVisibility(mLoadingView, state == STATE_LOADING);
        ViewUtils.fadeToVisibility(mContentView, state == STATE_CONTENT);
        ViewUtils.fadeToVisibility(mEmptyView, state == STATE_EMPTY);
        ViewUtils.fadeToVisibility(mErrorView, state == STATE_ERROR);
    }
}
