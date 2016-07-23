/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.profile.ui;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.broadcast.ui.BroadcastActivity;
import me.zhanghai.android.douya.network.api.info.apiv2.Broadcast;
import me.zhanghai.android.douya.network.api.info.apiv2.Image;
import me.zhanghai.android.douya.network.api.info.apiv2.Photo;
import me.zhanghai.android.douya.network.api.info.frodo.User;
import me.zhanghai.android.douya.ui.ContentStateLayout;
import me.zhanghai.android.douya.ui.FriendlyCardView;
import me.zhanghai.android.douya.ui.TimeActionTextView;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class ProfileDescriptionLayout extends FriendlyCardView {

    @BindView(R.id.title)
    TextView mTitleText;
    @BindView(R.id.description)
    TextView mDescriptionText;

    public ProfileDescriptionLayout(Context context) {
        super(context);

        init();
    }

    public ProfileDescriptionLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public ProfileDescriptionLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        inflate(getContext(), R.layout.profile_description_layout, this);
        ButterKnife.bind(this);
    }

    public void bind(String description) {
        OnClickListener viewFullListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                //context.startActivity(BroadcastListActivity.makeIntent(userIdOrUid));
            }
        };
        mTitleText.setOnClickListener(viewFullListener);
        mDescriptionText.setText(description);
        mDescriptionText.setOnClickListener(viewFullListener);
    }

    public void bind(User user) {
        bind(user.description);
    }
}
