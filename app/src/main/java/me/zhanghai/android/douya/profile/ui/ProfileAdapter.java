/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.profile.ui;

import androidx.recyclerview.widget.RecyclerView;

import me.zhanghai.android.douya.ui.BarrierAdapter;

public class ProfileAdapter extends BarrierAdapter {

    private ProfileDataAdapter mDataAdapter;

    public ProfileAdapter(ProfileIntroductionLayout.Listener listener) {
        super(new ProfileDataAdapter(listener));

        RecyclerView.Adapter<?>[] adapters = getAdapters();
        mDataAdapter = (ProfileDataAdapter) adapters[0];
    }

    public void setData(ProfileDataAdapter.Data data) {
        mDataAdapter.setData(data);
    }
}
