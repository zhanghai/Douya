/*
 * Copyright (c) 2019 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.compat

import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Base class for implementing an activity that is used to help implement an
 * AbstractAccountAuthenticator. If the AbstractAccountAuthenticator needs to use an activity
 * to handle the request then it can have the activity extend AccountAuthenticatorActivity.
 * The AbstractAccountAuthenticator passes in the response to the intent using the following:
 * <pre>
 * intent.putExtra([AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE], response);
 * </pre>
 * The activity then sets the result that is to be handed to the response via
 * [accountAuthenticatorResult].
 * This result will be sent as the result of the request when the activity finishes. If this
 * is never set or if it is set to null then error [AccountManager.ERROR_CODE_CANCELED]
 * will be called on the response.
 */
open class AccountAuthenticatorActivity : AppCompatActivity() {

    /**
     * The result that is to be sent as the result of the AbstractAccountAuthenticator request that
     * caused this Activity to be launched. If result is null then the request will be canceled.
     */
    var accountAuthenticatorResult: Bundle? = null

    private var accountAuthenticatorResponse: AccountAuthenticatorResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        accountAuthenticatorResponse = intent.getParcelableExtra(
                AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE)
        accountAuthenticatorResponse?.onRequestContinued()
    }

    override fun finish() {
        accountAuthenticatorResponse?.let {
            // send the result bundle back if set, otherwise send an error.
            if (accountAuthenticatorResult != null) {
                it.onResult(accountAuthenticatorResult)
            } else {
                it.onError(AccountManager.ERROR_CODE_CANCELED, "canceled")
            }
            accountAuthenticatorResponse = null
        }

        super.finish()
    }
}
