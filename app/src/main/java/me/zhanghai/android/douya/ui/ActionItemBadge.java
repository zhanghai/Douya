/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import androidx.core.view.ViewCompat;
import androidx.appcompat.content.res.AppCompatResources;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.TooltipUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class ActionItemBadge {

    public static void setup(MenuItem menuItem, Drawable icon, int count, Activity activity) {

        View actionView = menuItem.getActionView();
        actionView.setOnClickListener(view -> activity.onMenuItemSelected(
                Window.FEATURE_OPTIONS_PANEL, menuItem));
        CharSequence title = menuItem.getTitle();
        if (!TextUtils.isEmpty(title)) {
            actionView.setContentDescription(title);
            TooltipUtils.setup(actionView);
        }

        ImageView iconImage = actionView.findViewById(R.id.icon);
        iconImage.setImageDrawable(icon);

        TextView badgeText = actionView.findViewById(R.id.badge);
        Context themedContext = badgeText.getContext();
        ViewCompat.setBackground(badgeText, new BadgeDrawable(themedContext));
        badgeText.setTextColor(ViewUtils.getColorFromAttrRes(R.attr.colorPrimary, 0,
                themedContext));

        update(badgeText, count);
    }

    public static void setup(MenuItem menuItem, int iconResId, int count, Activity activity) {
        setup(menuItem, AppCompatResources.getDrawable(activity, iconResId), count, activity);
    }

    private static void update(TextView badgeText, int count) {
        boolean hasBadge = count > 0;
        // Don't set the badge count to 0 if we are fading away.
        if (hasBadge) {
            badgeText.setText(String.valueOf(count));
        }
        // We are using android:animateLayoutChanges="true", so no need animating here.
        ViewUtils.setVisibleOrGone(badgeText, hasBadge);
    }

    public static void update(MenuItem menuItem, int count) {
        update(menuItem.getActionView().<TextView>findViewById(R.id.badge), count);
    }

    private static class BadgeDrawable extends GradientDrawable {

        public BadgeDrawable(Context context) {
            setColor(ViewUtils.getColorFromAttrRes(R.attr.colorControlNormal, 0, context));
        }

        @Override
        public void setBounds(int left, int top, int right, int bottom) {
            super.setBounds(left, top, right, bottom);

            setCornerRadius(Math.min(right - left, bottom - top));
        }
    }
}
