/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class FragmentUtils {

    private FragmentUtils() {}

    public static Bundle ensureArguments(Fragment fragment) {
        Bundle arguments = fragment.getArguments();
        if (arguments == null) {
            arguments = new Bundle();
            fragment.setArguments(arguments);
        }
        return arguments;
    }

    @Deprecated
    public static <T> T findById(FragmentManager fragmentManager, int id) {
        //noinspection unchecked
        return (T) fragmentManager.findFragmentById(id);
    }

    public static <T> T findById(FragmentActivity activity, int id) {
        //noinspection deprecation
        return findById(activity.getSupportFragmentManager(), id);
    }

    public static <T> T findById(Fragment parentFragment, int id) {
        //noinspection deprecation
        return findById(parentFragment.getChildFragmentManager(), id);
    }

    @Deprecated
    public static <T> T findByTag(FragmentManager fragmentManager, String tag) {
        //noinspection unchecked
        return (T) fragmentManager.findFragmentByTag(tag);
    }

    public static <T> T findByTag(FragmentActivity activity, String tag) {
        //noinspection deprecation
        return findByTag(activity.getSupportFragmentManager(), tag);
    }

    public static <T> T findByTag(Fragment parentFragment, String tag) {
        //noinspection deprecation
        return findByTag(parentFragment.getChildFragmentManager(), tag);
    }

    @Deprecated
    public static void add(Fragment fragment, FragmentManager fragmentManager, int containerViewId,
                           String tag) {
        fragmentManager.beginTransaction()
                .add(containerViewId, fragment, tag)
                .commit();
    }

    @Deprecated
    public static void add(Fragment fragment, FragmentManager fragmentManager,
                           int containerViewId) {
        //noinspection deprecation
        add(fragment, fragmentManager, containerViewId, null);
    }

    public static void add(Fragment fragment, FragmentActivity activity, int containerViewId) {
        //noinspection deprecation
        add(fragment, activity.getSupportFragmentManager(), containerViewId);
    }

    public static void add(Fragment fragment, Fragment parentFragment, int containerViewId) {
        //noinspection deprecation
        add(fragment, parentFragment.getChildFragmentManager(), containerViewId);
    }

    @Deprecated
    public static void add(Fragment fragment, FragmentManager fragmentManager, String tag) {
        // Pass 0 as in {@link android.support.v4.app.BackStackRecord#add(Fragment, String)}.
        //noinspection deprecation
        add(fragment, fragmentManager, 0, tag);
    }

    public static void add(Fragment fragment, FragmentActivity activity, String tag) {
        //noinspection deprecation
        add(fragment, activity.getSupportFragmentManager(), tag);
    }

    public static void add(Fragment fragment, Fragment parentFragment, String tag) {
        //noinspection deprecation
        add(fragment, parentFragment.getChildFragmentManager(), tag);
    }

    public static void add(Fragment fragment, FragmentActivity activity) {
        add(fragment, activity.getSupportFragmentManager(), null);
    }

    public static void add(Fragment fragment, Fragment parentFragment) {
        add(fragment, parentFragment.getChildFragmentManager(), null);
    }

    public static void remove(Fragment fragment) {

        if (fragment.isRemoving()) {
            return;
        }

        fragment.getFragmentManager().beginTransaction()
                .remove(fragment)
                .commit();
    }
}
