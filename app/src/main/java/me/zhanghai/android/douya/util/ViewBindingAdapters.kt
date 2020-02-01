/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

import android.view.View
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.Dimension
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dev.chrisbanes.insetter.doOnApplyWindowInsets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.zhanghai.android.douya.api.info.Status
import me.zhanghai.android.douya.timeline.TimelineContentLayout
import me.zhanghai.android.douya.ui.MaterialSwipeRefreshLayout
import me.zhanghai.android.douya.ui.TimeTextView
import org.threeten.bp.ZonedDateTime
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
    GlobalScope.launch(Dispatchers.Main.immediate) { view.fadeToVisibility(visible, gone = true) }
}

@BindingAdapter("visibleOrInvisibleAnimated")
fun setViewVisibleOrInvisibleAnimated(view: View, visible: Boolean) {
    GlobalScope.launch(Dispatchers.Main.immediate) { view.fadeToVisibility(visible) }
}

@BindingAdapter("progressOffset")
fun setSwipeRefreshLayoutProgressOffset(
    swipeRefreshLayout: MaterialSwipeRefreshLayout,
    @Dimension progressOffset: Int
) {
    swipeRefreshLayout.progressOffset = progressOffset
}

@BindingAdapter("progressOffsetSizeAttr")
fun setSwipeRefreshLayoutProgressOffsetSizeAttr(
    swipeRefreshLayout: MaterialSwipeRefreshLayout,
    @AttrRes progressOffsetSizeAttr: Int
) = setSwipeRefreshLayoutProgressOffset(
    swipeRefreshLayout,
    swipeRefreshLayout.context.getDimensionPixelSizeByAttr(progressOffsetSizeAttr)
)

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

@BindingAdapter("refreshing")
fun setSwipeRefreshLayoutRefreshing(swipeRefreshLayout: SwipeRefreshLayout, refreshing: Boolean) {
    swipeRefreshLayout.isRefreshing = refreshing
}

@InverseBindingAdapter(attribute = "refreshing", event = "refreshingAttrChanged")
fun getSwipeRefreshLayoutRefreshing(swipeRefreshLayout: SwipeRefreshLayout) =
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

@BindingAdapter("textOrGone")
fun setTextViewTextOrGone(textView: TextView, text: CharSequence?) {
    textView.text = text
    textView.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
}

@BindingAdapter("textOrInvisible")
fun setTextViewTextOrInvisible(textView: TextView, text: CharSequence?) {
    textView.text = text
    textView.visibility = if (text.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
}

@BindingAdapter(value = [
    "textFormat", "textArg1", "textArg2", "textArg3", "textArg4", "textArg5", "textArg6",
    "textArg7", "textArg8", "textArg9", "textArg10"
], requireAll = false)
fun setTextViewTextFormatAndArgs(
    view: TextView,
    textFormat: String?,
    textArg1: Any?,
    textArg2: Any?,
    textArg3: Any?,
    textArg4: Any?,
    textArg5: Any?,
    textArg6: Any?,
    textArg7: Any?,
    textArg8: Any?,
    textArg9: Any?,
    textArg10: Any?
) {
    view.text = textFormat?.format(
        textArg1, textArg2, textArg3, textArg4, textArg5, textArg6, textArg7, textArg8, textArg9,
        textArg10
    )
}

@BindingAdapter("time")
fun setTimeTextViewTime(timeTextView: TimeTextView, time: ZonedDateTime?) {
    timeTextView.time = time
}

@BindingAdapter("status")
fun setTimelineContentLayoutStatus(timelineContentLayout: TimelineContentLayout, status: Status) {
    timelineContentLayout.bind(status)
}
