/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.os.Bundle;
import android.os.Parcelable;
import androidx.viewpager.widget.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

public abstract class ViewStatePagerAdapter extends PagerAdapter {

    private static final String KEY_PREFIX = ViewStatePagerAdapter.class.getName() + '.';

    private static final String STATE_VIEW_STATES_SIZE = KEY_PREFIX + "VIEW_STATES_SIZE";
    private static final String STATE_VIEW_STATE_PREFIX = KEY_PREFIX + "VIEW_STATE_";

    private SparseArray<View> mViews = new SparseArray<>();
    private SparseArray<SparseArray<Parcelable>> mViewStates = new SparseArray<>();

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public final View instantiateItem(ViewGroup container, int position) {
        View view = onCreateView(container, position);
        restoreViewState(position, view);
        mViews.put(position, view);
        return view;
    }

    protected abstract View onCreateView(ViewGroup container, int position);

    @Override
    public final void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        saveViewState(position, view);
        onDestroyView(container, position, view);
        mViews.remove(position);
        container.removeView(view);
    }

    protected abstract void onDestroyView(ViewGroup container, int position, View view);

    @Override
    public Parcelable saveState() {

        for (int i = 0, size = mViews.size(); i < size; ++i) {
            saveViewState(mViews.keyAt(i), mViews.valueAt(i));
        }

        Bundle bundle = new Bundle();
        int size = mViewStates.size();
        bundle.putInt(STATE_VIEW_STATES_SIZE, size);
        for (int i = 0; i < size; ++i) {
            bundle.putSparseParcelableArray(makeViewStateKey(mViewStates.keyAt(i)),
                    mViewStates.valueAt(i));
        }
        return bundle;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        Bundle bundle = (Bundle) state;
        bundle.setClassLoader(loader);
        int size = bundle.getInt(STATE_VIEW_STATES_SIZE);
        for (int i = 0; i < size; ++i) {
            mViewStates.put(i, bundle.getSparseParcelableArray(makeViewStateKey(i)));
        }
    }

    private String makeViewStateKey(int position) {
        return STATE_VIEW_STATE_PREFIX + position;
    }

    private void saveViewState(int position, View view) {
        SparseArray<Parcelable> viewState = new SparseArray<>();
        view.saveHierarchyState(viewState);
        mViewStates.put(position, viewState);
    }

    private void restoreViewState(int position, View view) {
        SparseArray<Parcelable> viewState = mViewStates.get(position);
        if (viewState != null) {
            view.restoreHierarchyState(viewState);
        }
    }
}
