/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.calendar.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.calendar.info.CalendarDay;
import me.zhanghai.android.douya.glide.AppWidgetTarget;
import me.zhanghai.android.douya.glide.GlideApp;
import me.zhanghai.android.douya.link.UriHandlerActivity;

public class CalendarAppWidgetProvider extends AppWidgetProvider {

    private static final AppWidgetTarget sPosterTarget = new AppWidgetTarget(R.id.poster);

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
        PendingIntent moviePendingIntent = PendingIntent.getActivity(context,
                calendarDay.url.hashCode(), UriHandlerActivity.makeIntent(calendarDay.url, context),
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.movie, moviePendingIntent);
        views.setTextViewText(R.id.title, calendarDay.getTitleText(context));
        views.setProgressBar(R.id.rating, calendarDay.getRatingProgressBarMax(),
                calendarDay.getProgressRatingBarProgress(), false);
        views.setTextViewText(R.id.rating_text, calendarDay.getRatingText(context));
        views.setTextViewText(R.id.event, calendarDay.getEventText(context));
        GlideApp.with(context)
                .asBitmap()
                .load(calendarDay.poster)
                .into(sPosterTarget.prepare(views, context, appWidgetIds));

        appWidgetManager.updateAppWidget(appWidgetIds, views);
    }
}
