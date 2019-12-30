/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import timber.log.Timber

lateinit var appContext: Context private set

class AppProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        appContext = context!!
        Timber.plant(Timber.DebugTree())
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?) = throw UnsupportedOperationException()

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ) = throw UnsupportedOperationException()

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ) = throw UnsupportedOperationException()

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?) =
        throw UnsupportedOperationException()

    override fun getType(uri: Uri) = throw UnsupportedOperationException()
}
