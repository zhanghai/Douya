/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.api.app

import me.zhanghai.android.douya.account.app.accountManager
import me.zhanghai.android.douya.account.app.activeAccount
import me.zhanghai.android.douya.api.info.ApiContract
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
        authenticator = if (account != null) ApiAuthenticator(account) else NoOpAuthenticator
    }

    private object NoOpAuthenticator : Authenticator {

        override fun authenticate(request: Request) = request

        @Suppress("ImplicitNullableNothingType")
        override fun retryAuthentication(response: Response) = null
    }
}
