/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArrayMap;
import androidx.collection.SparseArrayCompat;
import androidx.viewpager.widget.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

public abstract class ViewStatePagerAdapter extends PagerAdapter {

    private static final String KEY_PREFIX = ViewStatePagerAdapter.class.getName() + '.';

    private static final String STATE_VIEW_STATES_SIZE = KEY_PREFIX + "VIEW_STATES_SIZE";
    private static final String STATE_VIEW_STATE_PREFIX = KEY_PREFIX + "VIEW_STATE_";

    @NonNull
    private final SparseArrayCompat<View> mViews = new SparseArrayCompat<>();
    @NonNull
    private final ArrayMap<String, SparseArray<Parcelable>> mViewStates = new ArrayMap<>();

    @NonNull
    @Override
    public final View instantiateItem(@NonNull ViewGroup container, int position) {
        View view = onCreateView(container, position);
        restoreViewState(position, view);
        mViews.put(position, view);
        return view;
    }

    @NonNull
    protected abstract View onCreateView(@NonNull ViewGroup container, int position);

    @Override
    public final void destroyItem(@NonNull ViewGroup container, int position,
                                  @NonNull Object object) {
        View view = (View) object;
        saveViewState(position, view);
        onDestroyView(container, position, view);
        mViews.remove(position);
        container.removeView(view);
    }

    protected abstract void onDestroyView(@NonNull ViewGroup container, int position,
                                          @NonNull View view);

    @Override
    public final boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public final int getItemPosition(@NonNull Object object) {
        View view = (View) object;
        return getViewPosition(view);
    }

    protected int getViewPosition(@NonNull View view) {
        return POSITION_UNCHANGED;
    }

    @NonNull
    @Override
    public Parcelable saveState() {
        mViewStates.clear();
        for (int i = 0, size = mViews.size(); i < size; ++i) {
            saveViewState(mViews.keyAt(i), mViews.valueAt(i));
        }
        Bundle bundle = new Bundle();
        int size = mViewStates.size();
        bundle.putInt(STATE_VIEW_STATES_SIZE, size);
        for (int i = 0; i < size; ++i) {
            bundle.putSparseParcelableArray(mViewStates.keyAt(i), mViewStates.valueAt(i));
        }
        return bundle;
    }

    @Override
    public void restoreState(@NonNull Parcelable state, @Nullable ClassLoader loader) {
        Bundle bundle = (Bundle) state;
        bundle.setClassLoader(loader);
        int size = bundle.getInt(STATE_VIEW_STATES_SIZE);
        for (int i = 0; i < size; ++i) {
            String key = getViewStateKey(i);
            SparseArray<Parcelable> viewState = bundle.getSparseParcelableArray(key);
            mViewStates.put(key, viewState);
        }
    }

    private void saveViewState(int position, @NonNull View view) {
        String key = getViewStateKey(position);
        SparseArray<Parcelable> viewState = new SparseArray<>();
        view.saveHierarchyState(viewState);
        mViewStates.put(key, viewState);
    }

    private void restoreViewState(int position, @NonNull View view) {
        String key = getViewStateKey(position);
        SparseArray<Parcelable> viewState = mViewStates.get(key);
        if (viewState != null) {
            view.restoreHierarchyState(viewState);
        }
    }

    @NonNull
    private String getViewStateKey(int position) {
        return STATE_VIEW_STATE_PREFIX + getViewStateKeySuffix(position);
    }

    @NonNull
    protected String getViewStateKeySuffix(int position) {
        return Integer.toString(position);
    }
}
