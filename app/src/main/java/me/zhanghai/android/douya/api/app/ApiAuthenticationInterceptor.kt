/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.app

import me.zhanghai.android.douya.account.app.activeAccount
import me.zhanghai.android.douya.api.info.ApiContract
import me.zhanghai.android.douya.app.accountManager
import me.zhanghai.android.douya.network.AuthenticationInterceptor
import me.zhanghai.android.douya.setting.Settings
import okhttp3.Request
import okhttp3.Response

class ApiAuthenticationInterceptor : AuthenticationInterceptor(ApiContract.Api.MAX_AUTH_RETRIES) {
    override lateinit var authenticator: Authenticator

    init {
        updateAuthenticator()
        Settings.ACTIVE_ACCOUNT_NAME.observeForever { updateAuthenticator() }
    }

    private fun updateAuthenticator() {
        val account = accountManager.activeAccount
        authenticator = account?.let { ApiAuthenticator(it) } ?: NoOpAuthenticator
    }

    private object NoOpAuthenticator : Authenticator {
        override fun authenticate(request: Request): Request = request

        override fun retryAuthentication(response: Response): Request? = null
    }
}
