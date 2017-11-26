/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

public interface AppBarHost {

    void showAppBar();

    void hideAppBar();

    void setToolBarOnDoubleClickListener(DoubleClickToolbar.OnDoubleClickListener listener);
}
