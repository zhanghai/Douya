/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import com.google.android.material.navigation.NavigationView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.Window;
import android.widget.ImageView;

import me.zhanghai.android.douya.R;

public class TintHelper {

    private static final int[] CHECKED_STATE_SET = { android.R.attr.state_checked };
    private static final int[] DISABLED_STATE_SET = { -android.R.attr.state_enabled };
    private static final int[] EMPTY_STATE_SET = {};

    private TintHelper() {}

    public static void onPanelMenuCreated(int featureId, Menu menu, AppCompatActivity activity) {
        if (featureId == Window.FEATURE_OPTIONS_PANEL) {
            Context context = activity.getSupportActionBar().getThemedContext();
            ColorStateList menuTintList = ViewUtils.getColorStateListFromAttrRes(
                    R.attr.colorControlNormal, context);
            int popupThemeResId = ViewUtils.getResIdFromAttrRes(R.attr.popupTheme, 0, context);
            ColorStateList subMenuTintList;
            if (popupThemeResId != 0) {
                Context popupContext = new ContextThemeWrapper(context, popupThemeResId);
                subMenuTintList = ViewUtils.getColorStateListFromAttrRes(R.attr.colorControlNormal,
                        popupContext);
            } else {
                subMenuTintList = menuTintList;
            }
            tintMenuItemIcon(menu, menuTintList, subMenuTintList);
        }
    }

    private static void tintMenuItemIcon(Menu menu, ColorStateList menuTintList,
                                         ColorStateList subMenuTintList) {
        for (int i = 0, size = menu.size(); i < size; ++i) {
            MenuItem menuItem = menu.getItem(i);
            Drawable icon = menuItem.getIcon();
            if (icon != null) {
                icon = tintDrawable(icon, menuTintList);
                menuItem.setIcon(icon);
            }
            SubMenu subMenu = menuItem.getSubMenu();
            if (subMenu != null) {
                tintMenuItemIcon(subMenu, subMenuTintList, subMenuTintList);
            }
        }
    }

    public static void onSetSupportActionBar(Toolbar toolbar) {
        Drawable icon = toolbar.getNavigationIcon();
        ColorStateList tintList = ViewUtils.getColorStateListFromAttrRes(R.attr.colorControlNormal,
                toolbar.getContext());
        icon = tintDrawable(icon, tintList);
        toolbar.setNavigationIcon(icon);
    }

    public static Drawable tintDrawable(Drawable drawable, ColorStateList tintList) {
        drawable = DrawableCompat.wrap(drawable);
        drawable.mutate();
        DrawableCompat.setTintList(drawable, tintList);
        return drawable;
    }

    public static Drawable tintIcon(Drawable icon, Context context) {
        ColorStateList iconTintList = ViewUtils.getColorStateListFromAttrRes(
                R.attr.colorControlNormal, context);
        return tintDrawable(icon, iconTintList);
    }

    public static void tintImageViewIcon(ImageView imageView) {
        imageView.setImageDrawable(tintIcon(imageView.getDrawable(), imageView.getContext()));
    }

    public static void setNavigationItemTint(NavigationView navigationView, int color) {
        Context context = navigationView.getContext();
        navigationView.setItemIconTintList(createNavigationItemTintList(
                android.R.attr.textColorSecondary, color, context));
        navigationView.setItemTextColor(createNavigationItemTintList(
                android.R.attr.textColorPrimary, color, context));
    }

    private static ColorStateList createNavigationItemTintList(int baseColorAttrRes,
                                                               int primaryColor, Context context) {
        ColorStateList baseColor = ViewUtils.getColorStateListFromAttrRes(baseColorAttrRes,
                context);
        int defaultColor = baseColor.getDefaultColor();
        return new ColorStateList(new int[][] {
                DISABLED_STATE_SET,
                CHECKED_STATE_SET,
                EMPTY_STATE_SET
        }, new int[] {
                baseColor.getColorForState(DISABLED_STATE_SET, defaultColor),
                primaryColor,
                defaultColor
        });
    }
}
