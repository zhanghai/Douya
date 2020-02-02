/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.util

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.util.Patterns
import android.webkit.URLUtil
import me.zhanghai.android.douya.api.info.CommentAtEntity
import me.zhanghai.android.douya.api.info.ImageItem
import me.zhanghai.android.douya.api.info.SizedImage
import me.zhanghai.android.douya.api.info.Status
import me.zhanghai.android.douya.api.info.StatusCard
import me.zhanghai.android.douya.link.UriSpan
import me.zhanghai.android.douya.setting.Settings
import timber.log.Timber

val SizedImage.smallOrClosest: ImageItem?
    get() = small ?: normal ?: large ?: raw

val SizedImage.normalOrClosest: ImageItem?
    get() = normal ?: large ?: raw ?: small

val SizedImage.largeOrClosest: ImageItem?
    get() = large ?: raw ?: normal ?: small

val SizedImage.rawOrClosest: ImageItem?
    get() = raw ?: large ?: normal ?: small

val Status.textWithEntities: CharSequence
    get() = text.withEntities(entities)

val StatusCard.subtitleWithEntities: CharSequence
    get() = subtitle.withEntities(entities)

private fun String.withEntities(entities: List<CommentAtEntity>): CharSequence {
    if (isEmpty() || entities.isEmpty()) {
        return this
    }
    return SpannableStringBuilder().also {
        var lastIndex = 0
        for (entity in entities) {
            if (entity.start < 0 || entity.start >= length || entity.end < entity.start) {
                Timber.w("Ignoring malformed entity $entity")
                continue
            }
            if (entity.start < lastIndex) {
                Timber.w("Ignoring backward entity $entity, with lastIndex $lastIndex")
                continue
            }
            it.append(substring(lastIndex, entity.start))
            val entityText = if (Settings.SHOW_LONG_URL.value && URLUtil.isNetworkUrl(entity.title)
                && Patterns.WEB_URL.matcher(entity.title).matches()) {
                entity.uri
            } else {
                entity.title
            }
            val spannableEntityStart = it.length
            it
                .append(entityText)
                .setSpan(
                    UriSpan(entity.uri), spannableEntityStart,
                    spannableEntityStart + entityText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            lastIndex = entity.end
        }
        if (lastIndex != length) {
            it.append(substring(lastIndex, length))
        }
    }
}
