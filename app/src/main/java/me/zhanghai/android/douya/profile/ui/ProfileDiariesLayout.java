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
import me.zhanghai.android.douya.link.UriHandler;
import me.zhanghai.android.douya.network.api.info.apiv2.UserInfo;
import me.zhanghai.android.douya.network.api.info.frodo.Diary;
import me.zhanghai.android.douya.ui.FriendlyCardView;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.StringUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class ProfileDiariesLayout extends FriendlyCardView {

    private static final int DIARY_COUNT_MAX = 3;

    @BindView(R.id.title)
    TextView mTitleText;
    @BindView(R.id.diary_list)
    LinearLayout mDiaryList;
    @BindView(R.id.empty)
    View mEmptyView;
    @BindView(R.id.view_more)
    TextView mViewMoreText;

    public ProfileDiariesLayout(Context context) {
        super(context);

        init();
    }

    public ProfileDiariesLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public ProfileDiariesLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        ViewUtils.inflateInto(R.layout.profile_diaries_layout, this);
        ButterKnife.bind(this);
    }

    public void bind(final UserInfo userInfo, List<Diary> diaryList) {

        final Context context = getContext();
        OnClickListener viewMoreListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
                UriHandler.open(StringUtils.formatUs("https://www.douban.com/people/%s/notes",
                        userInfo.getIdOrUid()), context);
                //context.startActivity(DiaryListActivity.makeIntent(userInfo, context));
            }
        };
        mTitleText.setOnClickListener(viewMoreListener);
        mViewMoreText.setOnClickListener(viewMoreListener);

        int i = 0;
        for (final Diary diary : diaryList) {

            if (i >= DIARY_COUNT_MAX) {
                break;
            }

            if (i >= mDiaryList.getChildCount()) {
                LayoutInflater.from(context)
                        .inflate(R.layout.profile_diary_item, mDiaryList);
            }
            View diaryLayout = mDiaryList.getChildAt(i);
            DiaryLayoutHolder holder = (DiaryLayoutHolder) diaryLayout.getTag();
            if (holder == null) {
                holder = new DiaryLayoutHolder(diaryLayout);
                diaryLayout.setTag(holder);
                ViewUtils.setTextViewLinkClickable(holder.titleText);
            }

            if (!TextUtils.isEmpty(diary.cover)) {
                holder.coverImage.setVisibility(VISIBLE);
                ImageUtils.loadImage(holder.coverImage, diary.cover);
            } else {
                holder.coverImage.setVisibility(GONE);
            }
            holder.titleText.setText(diary.title);
            holder.abstractText.setText(diary.abstract_);
            diaryLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO
                    UriHandler.open(StringUtils.formatUs("https://www.douban.com/note/%d",
                            diary.id), context);
                    //context.startActivity(DiaryActivity.makeIntent(diary, context));
                }
            });

            ++i;
        }

        ViewUtils.setVisibleOrGone(mDiaryList, i != 0);
        ViewUtils.setVisibleOrGone(mEmptyView, i == 0);

        if (userInfo.diaryCount > i) {
            mViewMoreText.setText(context.getString(R.string.view_more_with_count_format,
                    userInfo.diaryCount));
        } else {
            mViewMoreText.setVisibility(GONE);
        }

        for (int count = mDiaryList.getChildCount(); i < count; ++i) {
            ViewUtils.setVisibleOrGone(mDiaryList.getChildAt(i), false);
        }
    }

    static class DiaryLayoutHolder {

        @BindView(R.id.cover)
        public ImageView coverImage;
        @BindView(R.id.title)
        public TextView titleText;
        @BindView(R.id.abstract_)
        public TextView abstractText;

        public DiaryLayoutHolder(View diaryLayout) {
            ButterKnife.bind(this, diaryLayout);
        }
    }
}
