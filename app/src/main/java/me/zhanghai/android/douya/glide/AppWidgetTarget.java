/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.glide;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.RemoteViews;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

/**
 * @see com.bumptech.glide.request.target.AppWidgetTarget
 */
public class AppWidgetTarget extends SimpleTarget<Bitmap> {

    private int viewId;
    private RemoteViews remoteViews;
    private Context context;
    private ComponentName componentName;
    private int[] widgetIds;

    public AppWidgetTarget(int viewId) {
        super(SIZE_ORIGINAL, SIZE_ORIGINAL);

        this.viewId = viewId;
    }

    private void prepare(RemoteViews remoteViews, Context context, ComponentName componentName,
                         int[] widgetIds) {
        this.remoteViews = remoteViews;
        this.context = context;
        this.componentName = componentName;
        this.widgetIds = widgetIds;
    }

    public AppWidgetTarget prepare(RemoteViews remoteViews, Context context,
                                   ComponentName componentName) {
        prepare(remoteViews, context, componentName, null);
        return this;
    }

    public AppWidgetTarget prepare(RemoteViews remoteViews, Context context, int[] widgetIds) {
        prepare(remoteViews, context, null, widgetIds);
        return this;
    }

    @Override
    public void onResourceReady(@NonNull Bitmap resource,
                                @Nullable Transition<? super Bitmap> transition) {
        remoteViews.setImageViewBitmap(viewId, resource);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        if (componentName != null) {
            appWidgetManager.updateAppWidget(componentName, remoteViews);
        } else {
            appWidgetManager.updateAppWidget(widgetIds, remoteViews);
        }
        clear();
    }

    @Override
    public void onLoadCleared(@Nullable Drawable placeholder) {
        clear();
    }

    @Override
    public void onLoadFailed(@Nullable Drawable errorDrawable) {
        clear();
    }

    private void clear() {
        remoteViews = null;
        context = null;
        componentName = null;
        widgetIds = null;
    }
}
