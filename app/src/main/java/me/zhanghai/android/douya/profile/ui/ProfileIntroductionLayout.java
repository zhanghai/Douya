/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.profile.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.network.api.info.apiv2.UserInfo;
import me.zhanghai.android.douya.ui.FriendlyCardView;

public class ProfileIntroductionLayout extends FriendlyCardView {

    @BindView(R.id.title)
    TextView mTitleText;
    @BindView(R.id.introduction)
    TextView mIntroductionText;

    public ProfileIntroductionLayout(Context context) {
        super(context);

        init();
    }

    public ProfileIntroductionLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public ProfileIntroductionLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        inflate(getContext(), R.layout.profile_introduction_layout, this);
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
        mIntroductionText.setText(description);
        mIntroductionText.setOnClickListener(viewFullListener);
    }

    public void bind(UserInfo userInfo) {
        bind(userInfo.introduction);
    }
}
