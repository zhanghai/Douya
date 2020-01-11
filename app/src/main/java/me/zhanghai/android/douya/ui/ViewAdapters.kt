/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.util.*

private val textViewInitialInputTypes = WeakHashMap<TextView, Int>()

@BindingAdapter("visibleOrGone")
fun setViewVisibleOrGone(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("visibleOrInvisible")
fun setViewVisibleOrInvisible(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("android:editable")
fun setTextViewEditable(textView: TextView, editable: Boolean) {
    val inputType = textViewInitialInputTypes.getOrPut(textView) { textView.inputType }
    if (editable) {
        textView.inputType = inputType
    } else {
        textView.keyListener = null
    }
}
