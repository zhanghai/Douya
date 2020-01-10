/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.util.*

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
