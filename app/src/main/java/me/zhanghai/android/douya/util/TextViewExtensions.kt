/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

import android.text.Editable
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout

fun TextView.hideTextInputLayoutErrorOnTextChange(textInputLayout: TextInputLayout) {
    addTextChangedListener(object : SimpleTextWatcher {
        override fun afterTextChanged(s: Editable) {
            textInputLayout.error = null
        }
    })
}

/** @see com.android.keyguard.KeyguardPasswordView#onEditorAction */
fun TextView.setOnEditorConfirmActionListener(listener: (TextView) -> Unit) {
    setOnEditorActionListener { view, actionId, event ->
        val isConfirmAction = if (event != null) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_SPACE,
                KeyEvent.KEYCODE_NUMPAD_ENTER -> true
                else -> false
            } && event.action == KeyEvent.ACTION_DOWN
        } else {
            when (actionId) {
                EditorInfo.IME_NULL, EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_NEXT -> true
                else -> false
            }
        }
        if (isConfirmAction) {
            listener(view)
            true
        } else {
            false
        }
    }
}

fun TextView.setSpanClickable() {
    val wasClickable = isClickable
    val wasLongClickable = isLongClickable
    movementMethod = ClickableMovementMethod
    // Reset for TextView.fixFocusableAndClickableSettings(). We don't want View.onTouchEvent()
    // to consume touch events.
    isClickable = wasClickable
    isLongClickable = wasLongClickable
}
