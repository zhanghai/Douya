/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.user.ui;

import me.zhanghai.android.douya.R;

public class UserAdapter extends BaseUserAdapter {

    public UserAdapter() {}

    @Override
    protected int getLayoutResource() {
        return R.layout.user_item;
    }
}
