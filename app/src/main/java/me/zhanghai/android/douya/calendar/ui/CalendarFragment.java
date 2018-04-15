/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.calendar.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.threeten.bp.DayOfWeek;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.calendar.info.CalendarDay;
import me.zhanghai.android.douya.link.UriHandler;
import me.zhanghai.android.douya.util.ImageUtils;
import me.zhanghai.android.douya.util.TintHelper;
import me.zhanghai.android.douya.util.ViewUtils;

public class CalendarFragment extends Fragment {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.date)
    TextView mDateText;
    @BindView(R.id.day_of_week)
    TextView mDayOfWeekText;
    @BindView(R.id.chinese_calendar_date)
    TextView mChineseCalendarDateText;
    @BindView(R.id.day_of_month)
    TextView mDayOfMonthText;
    @BindView(R.id.comment)
    TextView mCommentText;
    @BindView(R.id.movie)
    ViewGroup mMovieLayout;
    @BindView(R.id.title)
    TextView mTitleText;
    @BindView(R.id.rating)
    RatingBar mRatingBar;
    @BindView(R.id.rating_text)
    TextView mRatingText;
    @BindView(R.id.event)
    TextView mEventText;
    @BindView(R.id.poster)
    ImageView mPosterImage;

    public static CalendarFragment newInstance() {
        //noinspection deprecation
        return new CalendarFragment();
    }

    /**
     * @deprecated Use {@link #newInstance()} instead.
     */
    public CalendarFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.calendar_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        TintHelper.onSetSupportActionBar(mToolbar);

        // TODO
        CalendarDay calendarDay = new CalendarDay();
        calendarDay.date = "2018-03-06";
        //calendarDay.chineseCalendarDate = "正月十九";
        calendarDay.comment = "永不妥协，哪怕世界末日。";
        calendarDay.title = "守望者";
        calendarDay.rating = 8;
        calendarDay.event = "2009年3月6日，本片上映";
        calendarDay.poster = "https://img1.doubanio.com/view/photo/s_ratio_poster/public/p1663601927.webp";
        calendarDay.url = "https://movie.douban.com/subject/1972698/";

        mDateText.setText(calendarDay.getDateText(activity));
        mDayOfWeekText.setText(calendarDay.getDayOfWeekText(activity));
        mChineseCalendarDateText.setText(calendarDay.getChineseCalendarDateText());
        mDayOfMonthText.setText(calendarDay.getDayOfMonthText(activity));
        mDayOfMonthText.setTextColor(calendarDay.getDayOfMonthColor(mDayOfMonthText.getContext()));
        mCommentText.setText(calendarDay.comment);
        mMovieLayout.setOnClickListener(view -> UriHandler.open(calendarDay.url,
                view.getContext()));
        mTitleText.setText(calendarDay.getTitleText(activity));
        mRatingBar.setRating(calendarDay.getRatingBarRating());
        mRatingText.setText(calendarDay.getRatingText(activity));
        mEventText.setText(calendarDay.getEventText(activity));
        ImageUtils.loadImage(mPosterImage, calendarDay.poster);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
