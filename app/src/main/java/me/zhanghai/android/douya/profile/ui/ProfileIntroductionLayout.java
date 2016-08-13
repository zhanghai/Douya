/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.profile.ui;

import android.content.Context;
import android.text.TextUtils;
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
    @BindView(R.id.content)
    TextView mContentText;

    private Listener mListener;

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

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void bind(String introduction) {
        introduction = introduction.trim();
        final String trimmedIntroduction = introduction;
        if (!TextUtils.isEmpty(introduction)) {
            mTitleText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    onCopyText(trimmedIntroduction);
                }
            });
            mContentText.setText(introduction);
            mContentText.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onCopyText(trimmedIntroduction);
                    return true;
                }
            });
        } else {
            mContentText.setText(R.string.profile_introduction_empty);
        }
    }

    public void bind(UserInfo userInfo) {
        bind(userInfo.introduction);
    }

    private void onCopyText(String text) {
        if (mListener != null) {
            mListener.onCopyText(text);
        }
    }

    public interface Listener {
        void onCopyText(String text);
    }
}
