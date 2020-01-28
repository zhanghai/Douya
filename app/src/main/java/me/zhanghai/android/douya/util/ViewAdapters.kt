/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.WeakHashMap

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

private val textViewInitialInputTypes = WeakHashMap<TextView, Int>()

@BindingAdapter("android:editable")
fun setTextViewEditable(textView: TextView, editable: Boolean) {
    val inputType = textViewInitialInputTypes.getOrPut(textView) { textView.inputType }
    if (editable) {
        textView.inputType = inputType
    } else {
        textView.keyListener = null
    }
}
