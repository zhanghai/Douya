/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coil.api.clear
import coil.api.load
import coil.transform.CircleCropTransformation
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import me.zhanghai.android.douya.R
import me.zhanghai.android.douya.ui.MaterialSwipeRefreshLayout
import java.util.WeakHashMap

@BindingAdapter("backgroundScrim")
fun setViewBackgroundScrim(view: View, gravity: Int) {
    view.background = Drawables.createScrim(gravity)
}

@BindingAdapter("visibleOrGone")
fun setViewVisibleOrGone(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("visibleOrInvisible")
fun setViewVisibleOrInvisible(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("visibleOrGoneAnimated")
fun setViewVisibleOrGoneAnimated(view: View, visible: Boolean) {
    view.fadeToVisibilityUnsafe(visible, gone = true)
}

@BindingAdapter("visibleOrInvisibleAnimated")
fun setViewVisibleOrInvisibleAnimated(view: View, visible: Boolean) {
    view.fadeToVisibilityUnsafe(visible)
}

@BindingAdapter("url")
fun setImageViewUrl(imageView: ImageView, url: String?) {
    if (!url.isNullOrEmpty()) {
        imageView.load(url)
    } else {
        imageView.clear()
    }
}

@BindingAdapter("avatarUrl")
fun setImageViewAvatarUrl(imageView: ImageView, avatarUrl: String?) {
    if (!avatarUrl.isNullOrEmpty()) {
        imageView.load(avatarUrl) {
            placeholder(R.drawable.avatar_placeholder)
            transformations(CircleCropTransformation())
        }
    } else {
        imageView.clear()
    }
}

@BindingAdapter("progressOffsetSizeAttr")
fun setSwipeRefreshLayoutProgressOffsetSizeAttr(
    swipeRefreshLayout: MaterialSwipeRefreshLayout,
    @AttrRes progressOffsetSizeAttr: Int
) {
    swipeRefreshLayout.progressOffset =
        swipeRefreshLayout.context.getDimensionPixelSizeByAttr(progressOffsetSizeAttr)
}

private val swipeRefreshLayoutInitialProgressOffset = WeakHashMap<MaterialSwipeRefreshLayout, Int>()

@BindingAdapter("progressOffsetSystemWindowInsets")
fun setSwipeRefreshLayoutProgressOffsetSystemWindowInsets(
    swipeRefreshLayout: MaterialSwipeRefreshLayout,
    enabled: Boolean
) {
    swipeRefreshLayout.doOnApplyWindowInsets { _, insets, _ ->
        val initialProgressOffset = swipeRefreshLayoutInitialProgressOffset.getOrPut(
            swipeRefreshLayout
        ) { swipeRefreshLayout.progressOffset }
        swipeRefreshLayout.progressOffset = if (enabled) {
            initialProgressOffset + insets.systemWindowInsetTop
        } else {
            initialProgressOffset
        }
    }
}

@InverseBindingAdapter(attribute = "refreshing", event = "refreshingAttrChanged")
fun getSwipeRefreshLayoutRefreshing(swipeRefreshLayout: SwipeRefreshLayout): Boolean =
    swipeRefreshLayout.isRefreshing

@BindingAdapter(value = ["onRefresh", "refreshingAttrChanged"], requireAll = false)
fun setSwipeRefreshLayoutOnRefreshListener(
    swipeRefreshLayout: SwipeRefreshLayout,
    listener: SwipeRefreshLayout.OnRefreshListener?,
    refreshingAttrChanged: InverseBindingListener?
) {
    swipeRefreshLayout.setOnRefreshListener {
        listener?.onRefresh()
        refreshingAttrChanged?.onChange()
    }
}

private val textViewInitialInputTypes = WeakHashMap<TextView, Int>()

@BindingAdapter("android:editable")
fun setTextViewEditable(textView: TextView, editable: Boolean) {
    val initialInputType = textViewInitialInputTypes.getOrPut(textView) { textView.inputType }
    if (editable) {
        textView.inputType = initialInputType
    } else {
        textView.keyListener = null
    }
}

@BindingAdapter("spanClickable")
fun setTextViewSpanClickable(textView: TextView, spanClickable: Boolean) {
    if (spanClickable) {
        textView.setSpanClickable()
    }
}
