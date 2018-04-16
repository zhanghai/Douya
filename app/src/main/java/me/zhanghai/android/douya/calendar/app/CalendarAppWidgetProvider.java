/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.calendar.app;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.calendar.info.CalendarDay;

public class CalendarAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        CalendarDay calendarDay = CalendarDay.SAMPLE;

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.calendar_appwidget);
        views.setTextViewText(R.id.date, calendarDay.getDateText(context));
        views.setTextViewText(R.id.day_of_week, calendarDay.getDayOfWeekText(context));
        views.setTextViewText(R.id.chinese_calendar_date, calendarDay.getChineseCalendarDateText());
        views.setTextViewText(R.id.day_of_month, calendarDay.getDayOfMonthText(context));
        views.setTextColor(R.id.day_of_month, calendarDay.getDayOfMonthColor(context));
        views.setTextViewText(R.id.comment, calendarDay.comment);
        //views.setOnClickPendingIntent();mMovieLayout.setOnClickListener(view -> UriHandler.open(calendarDay.url,
        //        view.getContext()));
        views.setTextViewText(R.id.title, calendarDay.getTitleText(context));
        views.setProgressBar(R.id.rating, calendarDay.getRatingProgressBarMax(),
                calendarDay.getProgressRatingBarProgress(), false);
        views.setTextViewText(R.id.rating_text, calendarDay.getRatingText(context));
        views.setTextViewText(R.id.event, calendarDay.getEventText(context));
        //ImageUtils.loadImage(mPosterImage, calendarDay.poster);

        appWidgetManager.updateAppWidget(appWidgetIds, views);
    }
}
