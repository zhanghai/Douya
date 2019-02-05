/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.calendar.info;

import android.content.Context;
import androidx.core.content.ContextCompat;

import com.xhinliang.lunarcalendar.LunarCalendar;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.ViewUtils;

public class CalendarDay {

    public static final CalendarDay SAMPLE;
    static {
        SAMPLE = new CalendarDay();
        SAMPLE.date = "2018-03-06";
        //SAMPLE.chineseCalendarDate = "正月十九";
        SAMPLE.comment = "永不妥协，哪怕世界末日。";
        SAMPLE.title = "守望者";
        SAMPLE.rating = 8;
        SAMPLE.event = "2009年3月6日，本片上映";
        SAMPLE.poster = "https://img1.doubanio.com/view/photo/s_ratio_poster/public/p1663601927.webp";
        SAMPLE.url = "https://movie.douban.com/subject/1972698/";
    }

    public String date;

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

    private String getChineseCalendarDateText(LocalDate date) {
        LunarCalendar lunarCalendar = LunarCalendar.obtainCalendar(date.getYear(),
                date.getMonthValue(), date.getDayOfMonth());
        return lunarCalendar.getLunarMonth() + "月" + lunarCalendar.getLunarDay();
    }

    /**
     * @deprecated Use {@link #getChineseCalendarDateText(int)} instead.
     */
    public String getChineseCalendarDateText() {
        return getChineseCalendarDateText(getDate());
    }

    public String getChineseCalendarDateText(int year) {
        return getChineseCalendarDateText(getDate().withYear(year));
    }

    public String getDayOfMonthText(Context context) {
        LocalDate date = getDate();
        return context.getString(R.string.calendar_day_of_month_format, date.getDayOfMonth());
    }

    public int getDayOfMonthColor(int weekdayColor, int weekendColor) {
        DayOfWeek dayOfWeek = getDate().getDayOfWeek();
        boolean isWeekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
        return isWeekend ? weekendColor : weekdayColor;
    }

    public int getThemedDayOfMonthColor(Context context) {
        int weekdayColor = ViewUtils.getColorFromAttrRes(android.R.attr.textColorPrimary, 0,
                context);
        int weekendColor = ViewUtils.getColorFromAttrRes(android.R.attr.textColorHighlight, 0,
                context);
        return getDayOfMonthColor(weekdayColor, weekendColor);
    }

    public int getDayOfMonthColor(Context context) {
        int weekdayColor = ContextCompat.getColor(context,
                R.color.primary_text_default_material_light);
        int weekendColor = ContextCompat.getColor(context, R.color.calendar_highlight_text);
        return getDayOfMonthColor(weekdayColor, weekendColor);
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

    public int getProgressRatingBarProgress() {
        return Math.round(getRating());
    }

    public int getRatingProgressBarMax() {
        return 10;
    }

    public String getRatingText(Context context) {
        return context.getString(R.string.calendar_rating_format, getRating());
    }

    public String getEventText(Context context) {
        return context.getString(R.string.calendar_event_format, event);
    }
}
