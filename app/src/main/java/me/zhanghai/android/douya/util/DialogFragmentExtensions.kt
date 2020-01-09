/*
 * Copyright (c) 2020 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.util

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

fun DialogFragment.show(fragmentManager: FragmentManager) = show(fragmentManager, javaClass.name)

fun DialogFragment.show(fragmentTransaction: FragmentTransaction) =
    show(fragmentTransaction, javaClass.name)

fun DialogFragment.show(fragment: Fragment) = show(fragment.childFragmentManager)

fun DialogFragment.show(activity: FragmentActivity) = show(activity.supportFragmentManager)
