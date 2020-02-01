/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import me.zhanghai.android.douya.R

fun Fragment.showToast(textRes: Int, duration: Int = Toast.LENGTH_SHORT) =
    requireContext().showToast(textRes, duration)

fun Fragment.showToast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) =
    requireContext().showToast(text, duration)

fun Fragment.startActivitySafe(intent: Intent, options: Bundle? = null) {
    try {
        startActivity(intent, options)
    } catch (e: ActivityNotFoundException) {
        showToast(R.string.activity_not_found)
    }
}

fun Fragment.startActivityForResultSafe(intent: Intent, requestCode: Int, options: Bundle? = null) {
    try {
        startActivityForResult(intent, requestCode, options)
    } catch (e: ActivityNotFoundException) {
        showToast(R.string.activity_not_found)
    }
}
