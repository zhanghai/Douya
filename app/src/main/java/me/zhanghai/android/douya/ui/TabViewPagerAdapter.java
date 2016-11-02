/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class TabViewPagerAdapter extends PagerAdapter {

    private View[] mViews;
    private CharSequence[] mTitles;

    public TabViewPagerAdapter(View[] views, CharSequence[] titles) {
        init(views, titles);
    }

    public TabViewPagerAdapter(View[] views, int[] titleResIds, Context context) {

        CharSequence[] titles = new CharSequence[titleResIds.length];
        for (int i = 0; i < titleResIds.length; ++i) {
            titles[i] = context.getText(titleResIds[i]);
        }

        init(views, titles);
    }

    private void init(View[] views, CharSequence[] titles) {

        if (views.length != titles.length) {
            throw new IllegalArgumentException("View size and title size mismatch");
        }

        mViews = views;
        mTitles = titles;
    }

    @Override
    public int getCount() {
        return mViews.length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mViews[position];
        container.addView(view, position);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeViewAt(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
