/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import me.zhanghai.android.douya.app.application
import kotlin.reflect.KClass

fun <T : Activity> KClass<T>.createIntent() = Intent(application, java)

fun Uri.createViewIntent() = Intent(Intent.ACTION_VIEW, this)
