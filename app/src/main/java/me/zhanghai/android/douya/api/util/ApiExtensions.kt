/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.util

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import android.webkit.URLUtil
import me.zhanghai.android.douya.R
import me.zhanghai.android.douya.api.info.CommentAtEntity
import me.zhanghai.android.douya.api.info.IBaseFeedableItem
import me.zhanghai.android.douya.api.info.IUserAbstract
import me.zhanghai.android.douya.api.info.ImageItem
import me.zhanghai.android.douya.api.info.Owner
import me.zhanghai.android.douya.api.info.SizedImage
import me.zhanghai.android.douya.api.info.Status
import me.zhanghai.android.douya.api.info.StatusCard
import me.zhanghai.android.douya.api.info.Tag
import me.zhanghai.android.douya.compat.getDrawableCompat
import me.zhanghai.android.douya.link.FrodoUris
import me.zhanghai.android.douya.link.UriSpan
import me.zhanghai.android.douya.setting.Settings
import me.zhanghai.android.douya.util.DrawableSpan
import me.zhanghai.android.douya.util.SpaceSpan
import me.zhanghai.android.douya.util.getColorByAttr
import me.zhanghai.android.douya.util.takeIfNotEmpty
import timber.log.Timber

val IBaseFeedableItem.uriOrUrl: String
    get() = uri.ifEmpty { url }

val IUserAbstract.uriOrUrl: String
    get() = uri.ifEmpty { url }

val Owner.uriOrUrl: String
    get() = uri.ifEmpty { url }

val SizedImage.smallOrClosest: ImageItem?
    get() = small ?: normal ?: large ?: raw

val SizedImage.normalOrClosest: ImageItem?
    get() = normal ?: large ?: raw ?: small

val SizedImage.largeOrClosest: ImageItem?
    get() = large ?: raw ?: normal ?: small

val SizedImage.rawOrClosest: ImageItem?
    get() = raw ?: large ?: normal ?: small

val Status.activityCompat: String
    get() = activity.replace("转发", "转播")

val Status.parentStatusId: String
    get() = parentStatus?.id?.takeIfNotEmpty() ?: parentId

val Status.textWithEntities: CharSequence
    get() = text.withEntities(entities)

fun Status.textWithEntitiesAndParent(context: Context): CharSequence {
    val textWithEntities = textWithEntities
    var parentStatusId = parentStatusId
    if (parentStatus == null && parentStatusId.isEmpty()) {
        return textWithEntities
    }

    return SpannableStringBuilder.valueOf(textWithEntities).also {
        if (parentStatus != null) {
            val parentSpaceStartIndex = it.length
            it.append(" ")
            val parentSpaceEndIndex = it.length
            // HACK: For the case when rebroadcasting a broadcast.
            val spaceWidthEm = if (parentSpaceStartIndex > 0) 0.5f else -1f / 12
            it.setSpan(
                SpaceSpan(spaceWidthEm), parentSpaceStartIndex, parentSpaceEndIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            val parentIconStartIndex = it.length
            it.append(" ")
            val parentIconEndIndex = it.length
            val icon = context.getDrawableCompat(R.drawable.reshare_icon_18dp)
                .apply {
                    setTint(
                        context.getColorByAttr(
                            if (parentStatus.deleted) {
                                android.R.attr.textColorSecondary
                            } else {
                                android.R.attr.textColorLink
                            }
                        )
                    )
                }
            it.setSpan(
                DrawableSpan(icon), parentIconStartIndex, parentIconEndIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            if (parentStatus.deleted) {
                it.append(context.getString(R.string.timeline_item_reshared_status_deleted))
                val parentDeletedTextEndIndex = it.length
                it.setSpan(
                    ForegroundColorSpan(context.getColorByAttr(android.R.attr.textColorSecondary)),
                    parentSpaceStartIndex, parentDeletedTextEndIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                parentStatusId = ""
            } else {
                it.append(
                    context.getString(
                        R.string.timeline_item_text_resharer_format, parentStatus.author?.name ?: ""
                    )
                )
                val parentNameEndIndex = it.length
                it.setSpan(
                    UriSpan(parentStatus.uri), parentSpaceStartIndex, parentNameEndIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                it.append(parentStatus.textWithEntities)

                parentStatusId = parentStatus.parentStatusId
            }
        }

        if (parentStatusId == resharedStatus?.id) {
            parentStatusId = ""
        }

        if (parentStatusId.isNotEmpty()) {
            val parentSpaceStartIndex = it.length
            if (parentSpaceStartIndex > 0) {
                it.append(" ")
                val parentSpaceEndIndex = it.length
                it.setSpan(
                    SpaceSpan(0.5f), parentSpaceStartIndex, parentSpaceEndIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            it.append(context.getString(R.string.timeline_item_text_more_reshares))
            val parentMoreEndIndex = it.length
            it.setSpan(
                UriSpan(FrodoUris.createStatusUri(parentStatusId)), parentSpaceStartIndex,
                parentMoreEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
}

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

val StatusCard.uriOrUrl: String
    get() = uri.ifEmpty { url }

val Tag.uriOrUrl: String
    get() = uri.ifEmpty { url }
