/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.calendar.info;

import android.content.Context;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.ViewUtils;

public class CalendarDay {

    public String date;

    public String chineseCalendarDate;

    public String comment;

    public String title;

    public float rating;

    public String event;

    public String poster;

    public String url;


    private LocalDate getDate() {
        return LocalDate.parse(this.date);
    }

    public String getDateText(Context context) {
        LocalDate date = getDate();
        String datePattern = context.getString(R.string.calendar_date_pattern);
        return date.format(DateTimeFormatter.ofPattern(datePattern));
    }

    public String getDayOfWeekText(Context context) {
        String[] dayOfWeekNames = context.getResources().getStringArray(
                R.array.calendar_day_of_week_names);
        LocalDate date = getDate();
        return dayOfWeekNames[date.getDayOfWeek().getValue() - 1];
    }

    public String getDayOfMonthText(Context context) {
        LocalDate date = getDate();
        return context.getString(R.string.calendar_day_of_month_format, date.getDayOfMonth());
    }

    public int getDayOfMonthColor(Context context) {
        DayOfWeek dayOfWeek = getDate().getDayOfWeek();
        boolean isWeekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
        return ViewUtils.getColorFromAttrRes(isWeekend ? android.R.attr.textColorHighlight
                : android.R.attr.textColorPrimary, 0, context);
    }

    public String getTitleText(Context context) {
        return context.getString(R.string.calendar_title_format, title);
    }

    private float getRating() {
        return (float) Math.round(rating * 10) / 10;
    }

    public float getRatingBarRating() {
        return (float) Math.round(getRating()) / 2f;
    }

    public String getRatingText(Context context) {
        return context.getString(R.string.calendar_rating_format, getRating());
    }

    public String getEventText(Context context) {
        return context.getString(R.string.calendar_event_format, event);
    }
}
