/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.AppUtils;
import me.zhanghai.android.douya.util.MathUtils;
import me.zhanghai.android.materialedittext.internal.FloatProperty;

public class ProgressBarCompat {

    private static final int MAX_LEVEL = 10000;
    private static final int PROGRESS_ANIM_DURATION = 80;
    private static final DecelerateInterpolator PROGRESS_ANIM_INTERPOLATOR =
            new DecelerateInterpolator();

    private static final FloatProperty<ProgressBar> VISUAL_PROGRESS =
            new FloatProperty<ProgressBar>("visual_progress") {
                @Override
                public void setValue(ProgressBar object, float value) {
                    ProgressBarCompat.setVisualProgressCompat(object, android.R.id.progress, value);
                }
                @Override
                public Float get(ProgressBar object) {
                    return getVisualProgressCompat(object, android.R.id.progress);
                }
            };

    private ProgressBarCompat() {}

    public static void setProgress(final ProgressBar progressBar, int progress, boolean animate) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            progressBar.setProgress(progress, animate);
            return;
        }
        if (!animate) {
            progressBar.setProgress(progress);
            return;
        }

        if (progressBar.isIndeterminate()) {
            return;
        }
        progress = MathUtils.constrain(progress, 0, progressBar.getMax());
        if (progress == progressBar.getProgress()) {
            // No change from current.
            return;
        }

        final int finalProgress = progress;
        AppUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                float savedVisualProgress = getVisualProgressCompat(progressBar,
                        android.R.id.progress);
                progressBar.setProgress(finalProgress);
                setVisualProgressCompat(progressBar, android.R.id.progress, savedVisualProgress);
                int max = progressBar.getMax();
                float scale = max > 0 ? (float) finalProgress /  max : 0;
                ObjectAnimator animator = ObjectAnimator.ofFloat(progressBar, VISUAL_PROGRESS,
                        scale);
                animator.setDuration(PROGRESS_ANIM_DURATION);
                animator.setInterpolator(PROGRESS_ANIM_INTERPOLATOR);
                //animator.setAutoCancel(true);
                ObjectAnimator oldAnimator = (ObjectAnimator) progressBar.getTag(
                        R.id.progress_bar_compat_object_animator_tag);
                if (oldAnimator != null) {
                    oldAnimator.cancel();
                }
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        progressBar.setTag(R.id.progress_bar_compat_object_animator_tag, null);
                    }
                });
                progressBar.setTag(R.id.progress_bar_compat_object_animator_tag, animator);
                animator.start();
            }
        });
    }

    private static float getVisualProgressCompat(ProgressBar progressBar, int id) {
        Drawable drawable = getVisualProgressDrawable(progressBar, id);
        if (drawable != null) {
            return (float) drawable.getLevel() / MAX_LEVEL;
        } else if (id == android.R.id.secondaryProgress) {
            return (float) progressBar.getSecondaryProgress() / progressBar.getMax();
        } else {
            return (float) progressBar.getProgress() / progressBar.getMax();
        }
    }

    /*
     * @see ProgressBar#setVisualProgress(int, float)
     */
    private static void setVisualProgressCompat(ProgressBar progressBar, int id, float progress) {
        Drawable drawable = getVisualProgressDrawable(progressBar, id);
        if (drawable != null) {
            int level = (int) (progress * MAX_LEVEL);
            drawable.setLevel(level);
        } else {
            progressBar.invalidate();
        }
    }

    private static Drawable getVisualProgressDrawable(ProgressBar progressBar, int id) {
        Drawable currentDrawable = progressBar.isIndeterminate() ?
                progressBar.getIndeterminateDrawable() : progressBar.getProgressDrawable();
        Drawable drawable = currentDrawable;
        if (drawable instanceof LayerDrawable) {
            drawable = ((LayerDrawable) drawable).findDrawableByLayerId(id);
            if (drawable == null) {
                // If we can't find the requested layer, fall back to setting
                // the level of the entire drawable. This will break if
                // progress is set on multiple elements, but the theme-default
                // drawable will always have all layer IDs present.
                drawable = currentDrawable;
            }
        }
        return drawable;
    }
}
