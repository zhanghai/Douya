/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.File;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.glide.GlideApp;
import me.zhanghai.android.douya.network.api.info.frodo.Image;
import me.zhanghai.android.douya.network.api.info.frodo.ImageWithSize;
import me.zhanghai.android.douya.network.api.info.frodo.Photo;
import me.zhanghai.android.douya.ui.RatioImageView;

public class ImageUtils {

    private static final RequestOptions REQUEST_OPTIONS_LOAD_AVATAR = new RequestOptions()
            .placeholder(R.drawable.avatar_icon_40dp)
            .dontTransform();

    public static void loadAvatar(ImageView view, String url) {
        GlideApp.with(view.getContext())
                .load(url)
                .apply(REQUEST_OPTIONS_LOAD_AVATAR)
                .into(view);
    }

    private static final RequestOptions REQUEST_OPTIONS_LOAD_NAVIGATION_HEADER_AVATAR =
            new RequestOptions()
                    .placeholder(R.drawable.avatar_icon_white_inactive_64dp)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontTransform();

    public static void loadNavigationHeaderAvatar(final ImageView view, final String url) {
        Context context = view.getContext();
        int size = context.getResources().getDimensionPixelSize(
                R.dimen.navigation_header_avatar_size);
        GlideApp.with(context)
                .load(url)
                .apply(REQUEST_OPTIONS_LOAD_NAVIGATION_HEADER_AVATAR
                        .override(size, size))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        (e != null ? e : new NullPointerException()).printStackTrace();
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        view.setTag(url);
                        return false;
                    }
                })
                .into(view);
    }

    private static final RequestOptions REQUEST_OPTIONS_LOAD_NAVIGATION_ACCOUNT_LIST_AVATAR =
            new RequestOptions()
                    .placeholder(R.drawable.avatar_icon_40dp)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontTransform();

    public static void loadNavigationAccountListAvatar(ImageView view, String url) {
        Context context = view.getContext();
        int size = context.getResources().getDimensionPixelSize(
                R.dimen.navigation_header_avatar_size);
        GlideApp.with(context)
                .load(url)
                .apply(REQUEST_OPTIONS_LOAD_NAVIGATION_ACCOUNT_LIST_AVATAR
                        .override(size, size))
                .into(view);
    }

    private static final RequestOptions REQUEST_OPTIONS_LOAD_PROFILE_AVATAR =
            new RequestOptions()
                    .dontTransform();

    public static void loadProfileAvatarAndFadeIn(final ImageView view, String url) {
        GlideApp.with(view.getContext())
                .load(url)
                .apply(REQUEST_OPTIONS_LOAD_PROFILE_AVATAR)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        (e != null ? e : new NullPointerException()).printStackTrace();
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model,
                                                   Target<Drawable> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        ViewUtils.fadeIn(view);
                        return false;
                    }
                })
                .into(view);
    }

    private static final RequestOptions REQUEST_OPTIONS_LOAD_IMAGE = new RequestOptions()
            .dontTransform()
            .placeholder(android.R.color.transparent);

    public static void loadImage(ImageView view, String url, RequestListener<Drawable> listener) {
        GlideApp.with(view.getContext())
                .load(url)
                .apply(REQUEST_OPTIONS_LOAD_IMAGE)
                .transition(DrawableTransitionOptions.withCrossFade(ViewUtils.getShortAnimTime(
                        view)))
                .listener(listener)
                .into(view);
    }

    public static void loadImage(ImageView view, String url) {
        loadImage(view, url, null);
    }

    public static void loadImage(ImageView view, Image image) {
        loadImage(view, image.getNormal());
    }

    public static void loadImage(ImageView view, ImageWithSize image) {
        loadImage(view, image.getNormal().url);
    }

    public static void loadImage(ImageView view, Photo photo) {
        loadImage(view, photo.image);
    }

    public static void loadImageFile(ImageView view, File file,
                                     RequestListener<Drawable> listener) {
        GlideApp.with(view.getContext())
                .load(file)
                .apply(REQUEST_OPTIONS_LOAD_IMAGE)
                .transition(DrawableTransitionOptions.withCrossFade(ViewUtils.getShortAnimTime(
                        view)))
                .listener(listener)
                .into(view);
    }

    private static final RequestOptions REQUEST_OPTIONS_LOAD_IMAGE_WITH_RATIO = new RequestOptions()
            // dontTransform() is required for our RatioImageView to work correctly.
            .dontTransform()
            .placeholder(android.R.color.transparent);

    public static void loadImageWithRatio(RatioImageView view, ImageWithSize image) {
        ImageWithSize.Item imageItem = image.getNormal();
        view.setRatio(imageItem.width, imageItem.height);
        GlideApp.with(view.getContext())
                .load(imageItem.url)
                .apply(REQUEST_OPTIONS_LOAD_IMAGE_WITH_RATIO)
                .transition(DrawableTransitionOptions.withCrossFade(ViewUtils.getShortAnimTime(
                        view)))
                .into(view);
    }

    public static void loadImageWithRatio(RatioImageView view, Photo photo) {
        loadImageWithRatio(view, photo.image);
    }

    public static void loadImageWithRatio(RatioImageView view,
                                          me.zhanghai.android.douya.network.api.info.apiv2.Image image) {
        view.setRatio(image.width, image.height);
        GlideApp.with(view.getContext())
                .load(image.medium)
                .apply(REQUEST_OPTIONS_LOAD_IMAGE_WITH_RATIO)
                .transition(DrawableTransitionOptions.withCrossFade(ViewUtils.getShortAnimTime(
                        view)))
                .into(view);
    }
}
