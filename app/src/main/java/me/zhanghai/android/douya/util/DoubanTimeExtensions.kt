/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

import android.content.Context
import me.zhanghai.android.douya.R
import org.threeten.bp.Duration
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.chrono.IsoChronology
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeFormatterBuilder
import org.threeten.bp.format.ResolverStyle

private val DOUBAN_DATE_TIME_FORMATTER = DateTimeFormatterBuilder()
    .append(DateTimeFormatter.ISO_LOCAL_DATE)
    .appendLiteral(' ')
    .append(DateTimeFormatter.ISO_LOCAL_TIME)
    .toFormatter()
    .withResolverStyle(ResolverStyle.STRICT)
    .withChronology(IsoChronology.INSTANCE)

private val DOUBAN_ZONE_ID = ZoneId.of("Asia/Shanghai")

object ZonedDateTimes

fun ZonedDateTimes.parseDouban(text: String): ZonedDateTime =
    ZonedDateTime.of(LocalDateTime.parse(text, DOUBAN_DATE_TIME_FORMATTER), DOUBAN_ZONE_ID)

fun ZonedDateTime.formatDouban(): String = toLocalDateTime().format(DOUBAN_DATE_TIME_FORMATTER)

private val JUST_NOW_DURATION_RANGE = Duration.ZERO comparableUntil Duration.ofMinutes(1)
private val MINUTE_FORMAT_DURATION_RANGE = Duration.ofMinutes(1) comparableUntil Duration.ofHours(1)
private val HOUR_FORMAT_DURATION_RANGE = Duration.ofHours(1) comparableUntil Duration.ofHours(2)

fun ZonedDateTime.formatHumanFriendly(context: Context): String {
    val date = toLocalDate()
    val now = ZonedDateTime.now().withZoneSameInstant(zone)
    val nowDate = now.toLocalDate()
    return if (date == nowDate) {
        when (val duration = Duration.between(this, now)) {
            in JUST_NOW_DURATION_RANGE -> context.getString(R.string.just_now)
            in MINUTE_FORMAT_DURATION_RANGE ->
                context.getString(R.string.minute_format, duration.toMinutes())
            in HOUR_FORMAT_DURATION_RANGE ->
                context.getString(R.string.hour_format, duration.toHours())
            else -> DateTimeFormatter
                .ofPattern(context.getString(R.string.today_hour_minute_pattern))
                .format(this)
        }
    } else {
        DateTimeFormatter
            .ofPattern(
                context.getString(
                    when {
                        date.plusDays(1) == nowDate -> R.string.yesterday_hour_minute_pattern
                        date.year == nowDate.year -> R.string.month_day_hour_minute_pattern
                        else -> R.string.date_hour_minute_pattern
                    }
                )
            )
            .format(this)
    }
}

fun LocalDate.formatHumanFriendly(zone: ZoneId, context: Context): String {
    val now = ZonedDateTime.now().withZoneSameInstant(zone)
    val nowDate: LocalDate = now.toLocalDate()
    return when {
        this == nowDate -> context.getString(R.string.today)
        plusDays(1) == nowDate -> context.getString(R.string.yesterday)
        else -> DateTimeFormatter
            .ofPattern(
                context.getString(
                    if (year == nowDate.year) R.string.month_day_pattern else R.string.date_pattern
                )
            )
            .format(this)
    }
}

fun ZonedDateTime.formatDateHumanFriendly(context: Context): String =
    toLocalDate().formatHumanFriendly(zone, context)
