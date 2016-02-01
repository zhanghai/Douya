/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.support.v7.widget.DefaultItemAnimator;

/**
 * A DefaultItemAnimator with setSupportsChangeAnimations(false).
 */
public class NoChangeAnimationItemAnimator extends DefaultItemAnimator {

    public NoChangeAnimationItemAnimator() {
        setSupportsChangeAnimations(false);
    }
}
