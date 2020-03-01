/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun FragmentManager.awaitViewCreated(fragment: Fragment): View =
    suspendCancellableCoroutine { continuation ->
        val callbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentViewCreated(
                fragmentManager: FragmentManager,
                fragment2: Fragment,
                view: View,
                savedInstanceState: Bundle?
            ) {
                if (fragment2 === fragment || !continuation.isActive) {
                    unregisterFragmentLifecycleCallbacks(this)
                }
                if (fragment2 === fragment && continuation.isActive) {
                    continuation.resume(view)
                }
            }
        }
        continuation.invokeOnCancellation { unregisterFragmentLifecycleCallbacks(callbacks) }
        registerFragmentLifecycleCallbacks(callbacks, false)
    }
