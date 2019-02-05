/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.gallery.ui;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.Nullable;
import androidx.collection.SparseArrayCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.glide.GlideApp;
import me.zhanghai.android.douya.glide.info.ImageInfo;
import me.zhanghai.android.douya.glide.progress.ProgressListener;
import me.zhanghai.android.douya.ui.ProgressBarCompat;
import me.zhanghai.android.douya.ui.SaveStateSubsamplingScaleImageView;
import me.zhanghai.android.douya.ui.ViewStatePagerAdapter;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class GalleryAdapter extends ViewStatePagerAdapter {

    private List<Uri> mImageList;
    private Listener mListener;
    private SparseArrayCompat<File> mFileMap = new SparseArrayCompat<>();

    public GalleryAdapter(List<Uri> imageList, Listener listener) {
        mImageList = imageList;
        mListener = listener;
    }

    @Override
    public int getCount() {
        return mImageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public View onCreateView(ViewGroup container, int position) {
        final View layout = ViewUtils.inflate(R.layout.gallery_item, container);
        final ViewHolder holder = new ViewHolder(layout);
        layout.setTag(holder);
        holder.image.setOnPhotoTapListener((view, x, y) -> {
            if (mListener != null) {
                mListener.onTap();
            }
        });
        holder.largeImage.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onTap();
            }
        });
        loadImageForPosition(position, holder);
        container.addView(layout);
        return layout;
    }

    private void loadImageForPosition(int position, ViewHolder holder) {
        ViewUtils.fadeIn(holder.progress);
        GlideApp.with(holder.progress.getContext())
                .downloadOnlyDefaultPriority()
                .load(mImageList.get(position))
                .progressListener(new ProgressListener() {
                    @Override
                    public void onProgress(long bytesRead, long contentLength, boolean done) {
                        int progress = Math.round((float) bytesRead / contentLength
                                * holder.progress.getMax());
                        ProgressBarCompat.setProgress(holder.progress, progress, true);
                    }
                })
                .listener(new RequestListener<File>() {
                    @Override
                    public boolean onResourceReady(File resource, Object model, Target<File> target,
                                                   DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<File> target, boolean isFirstResource) {
                        showError(e, R.string.gallery_network_error, holder);
                        return false;
                    }
                })
                .into(new SimpleTarget<File>() {
                    @Override
                    public void onResourceReady(File file,
                                                Transition<? super File> transition) {
                        mFileMap.put(position, file);
                        if (mListener != null) {
                            mListener.onFileDownloaded(position);
                        }
                        holder.progress.setIndeterminate(true);
                        loadImageFromFile(file, holder);
                    }
                });
    }

    private void loadImageFromFile(final File file, final ViewHolder holder) {
        GlideApp
                .with(holder.progress.getContext())
                .asInfo()
                .load(file)
                .listener(new RequestListener<ImageInfo>() {
                    @Override
                    public boolean onResourceReady(ImageInfo resource, Object model,
                                                   Target<ImageInfo> target, DataSource dataSource,
                                                   boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<ImageInfo> target, boolean isFirstResource) {
                        showError(e, R.string.gallery_load_error, holder);
                        return false;
                    }
                })
                .into(new SimpleTarget<ImageInfo>() {
                    @Override
                    public void onResourceReady(ImageInfo imageInfo,
                                                Transition<? super ImageInfo> transition) {
                        loadImageIntoView(file, imageInfo, holder);
                    }
                });
    }

    private void loadImageIntoView(File file, ImageInfo imageInfo, final ViewHolder holder) {
        if (!shouldUseLargeImageView(imageInfo)) {
            // Otherwise SizeReadyCallback.onSizeReady() is never called.
            ViewUtils.setVisibleOrGone(holder.image, true);
            ImageUtils.loadImageFile(holder.image, file, new RequestListener<Drawable>() {
                @Override
                public boolean onResourceReady(Drawable drawable, Object model,
                                               Target<Drawable> target, DataSource dataSource,
                                               boolean isFirstResource) {
                    ViewUtils.fadeOut(holder.progress);
                    ViewUtils.setVisibleOrGone(holder.image, true);
                    return false;
                }
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                            Target<Drawable> target, boolean isFirstResource) {
                    showError(e, R.string.gallery_load_error, holder);
                    return false;
                }
            });
        } else {
            holder.largeImage.setDoubleTapZoomDuration(300);
            holder.largeImage.setOrientation(SubsamplingScaleImageView.ORIENTATION_USE_EXIF);
            // Otherwise OnImageEventListener.onReady() is never called.
            ViewUtils.setVisibleOrGone(holder.largeImage, true);
            holder.largeImage.setAlpha(0);
            holder.largeImage.setOnImageEventListener(
                    new SubsamplingScaleImageView.DefaultOnImageEventListener() {
                        @Override
                        public void onReady() {
                            int viewWidth = holder.largeImage.getWidth()
                                    - holder.largeImage.getPaddingLeft()
                                    - holder.largeImage.getPaddingRight();
                            int viewHeight = holder.largeImage.getHeight()
                                    - holder.largeImage.getPaddingTop()
                                    - holder.largeImage.getPaddingBottom();
                            int orientation = holder.largeImage.getAppliedOrientation();
                            boolean rotated90Or270 =
                                    orientation == SubsamplingScaleImageView.ORIENTATION_90
                                            || orientation
                                            == SubsamplingScaleImageView.ORIENTATION_270;
                            int imageWidth = rotated90Or270 ? holder.largeImage.getSHeight()
                                    : holder.largeImage.getSWidth();
                            int imageHeight = rotated90Or270 ? holder.largeImage.getSWidth()
                                    : holder.largeImage.getSHeight();
                            float cropScale = Math.max((float) viewWidth / imageWidth,
                                    (float) viewHeight / imageHeight);
                            holder.largeImage.setDoubleTapZoomScale(cropScale);
                            ViewUtils.crossfade(holder.progress, holder.largeImage);
                        }
                        @Override
                        public void onImageLoadError(Exception e) {
                            e.printStackTrace();
                            showError(e, R.string.gallery_load_error, holder);
                        }
                    });
            holder.largeImage.setImageRestoringSavedState(ImageSource.uri(Uri.fromFile(file)));
        }
    }

    private boolean shouldUseLargeImageView(ImageInfo imageInfo) {
        // See BitmapFactory.cpp encodedFormatToString()
        if (TextUtils.equals(imageInfo.mimeType, "image/gif")) {
            return false;
        }
        if (imageInfo.width <= 0 || imageInfo.height <= 0) {
            return false;
        }
        if (imageInfo.width > 2048 || imageInfo.height > 2048) {
            float ratio = (float) imageInfo.width / imageInfo.height;
            if (ratio < 0.5 || ratio > 2) {
                return true;
            }
        }
        return false;
    }

    private void showError(@Nullable Exception e, int resId, ViewHolder holder) {
        (e != null ? e : new NullPointerException()).printStackTrace();
        holder.errorText.setText(resId);
        ViewUtils.crossfade(holder.progress, holder.errorText);
    }

    @Override
    public void onDestroyView(ViewGroup container, int position, View view) {
        ViewHolder holder = (ViewHolder) view.getTag();
        GlideApp.with(holder.image).clear(holder.image);
        container.removeView(view);
    }

    public File getFile(int position) {
        return mFileMap.get(position);
    }

    public interface Listener {
        void onTap();
        void onFileDownloaded(int position);
    }

    static class ViewHolder {

        @BindView(R.id.image)
        public PhotoView image;
        @BindView(R.id.largeImage)
        public SaveStateSubsamplingScaleImageView largeImage;
        @BindView(R.id.error)
        public TextView errorText;
        @BindView(R.id.progress)
        public ProgressBar progress;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
