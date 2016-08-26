/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.account.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.ParseError;
import com.android.volley.VolleyError;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.account.info.AccountContract;
import me.zhanghai.android.douya.account.util.AccountUtils;
import me.zhanghai.android.douya.app.RetainDataFragment;
import me.zhanghai.android.douya.network.RequestFragment;
import me.zhanghai.android.douya.network.api.ApiContract.Response.Error.Codes.Token;
import me.zhanghai.android.douya.network.api.ApiError;
import me.zhanghai.android.douya.network.api.TokenRequest;
import me.zhanghai.android.douya.util.LogUtils;
import me.zhanghai.android.douya.util.ViewUtils;

public class AuthenticatorActivity extends AppCompatAccountAuthenticatorActivity
        implements RequestFragment.Listener<TokenRequest.Result, Void> {

    private static final String KEY_PREFIX = AuthenticatorActivity.class.getName() + '.';

    // NOTE: EXTRA_AUTH_MODE and EXTRA_AUTH_TOKEN_TYPE must be supplied.
    public static final String EXTRA_AUTH_MODE = KEY_PREFIX + "auth_mode";
    public static final String EXTRA_USERNAME = KEY_PREFIX + "username";
    // NOTE: EXTRA_PASSWORD should be the original password without obfuscation.
    public static final String EXTRA_PASSWORD = KEY_PREFIX + "password";

    public static final String AUTH_MODE_NEW = "new";
    public static final String AUTH_MODE_ADD = "add";
    public static final String AUTH_MODE_UPDATE = "update";
    public static final String AUTH_MODE_CONFIRM = "confirm";

    private static final String STATE_KEY_USERNAME_LAYOUT_ERROR = KEY_PREFIX
            + "username_layout_error";
    private static final String STATE_KEY_PASSWORD_LAYOUT_ERROR = KEY_PREFIX
            + "password_layout_error";

    private static final int REQUEST_CODE_AUTH = 0;

    private static final String RETAIN_DATA_KEY_USERNAME = KEY_PREFIX + "username";
    private static final String RETAIN_DATA_KEY_PASSWORD = KEY_PREFIX + "password";
    private static final String RETAIN_DATA_KEY_VIEW_STATE = KEY_PREFIX + "view_state";

    @BindView(R.id.form)
    View mFormLayout;
    @BindView(R.id.username_layout)
    TextInputLayout mUsernameLayout;
    @BindView(R.id.username)
    EditText mUsernameEdit;
    @BindView(R.id.password_layout)
    TextInputLayout mPasswordLayout;
    @BindView(R.id.password)
    EditText mPasswordEdit;
    @BindView(R.id.login)
    Button mLoginButton;
    @BindView(R.id.progress)
    ProgressBar mProgress;

    private RetainDataFragment mRetainDataFragment;

    private String mAuthMode;
    private String mUsername;
    private String mPassword;

    // For ViewState, because we are crossfading views.
    private boolean mShowingProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRetainDataFragment = RetainDataFragment.attachTo(this);

        Intent intent = getIntent();
        mAuthMode = intent.getStringExtra(EXTRA_AUTH_MODE);
        mUsername = mRetainDataFragment.remove(RETAIN_DATA_KEY_USERNAME);
        if (mUsername == null) {
            mUsername = intent.getStringExtra(EXTRA_USERNAME);
        }
        mPassword = mRetainDataFragment.remove(RETAIN_DATA_KEY_PASSWORD);
        if (mPassword == null) {
            mPassword = intent.getStringExtra(EXTRA_PASSWORD);
        }

        setContentView(R.layout.authenticator_activity);
        ButterKnife.bind(this);

        // TODO: Make the card slide in from bottom.

        if (!TextUtils.isEmpty(mUsername)) {
            mUsernameEdit.setText(mUsername);
        }
        ViewUtils.hideTextInputLayoutErrorOnTextChange(mUsernameEdit, mUsernameLayout);

        if (!TextUtils.isEmpty(mPassword)) {
            mPasswordEdit.setText(mPassword);
        }
        ViewUtils.hideTextInputLayoutErrorOnTextChange(mPasswordEdit, mPasswordLayout);
        mPasswordEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.ime_login || id == EditorInfo.IME_ACTION_DONE
                        || id == EditorInfo.IME_NULL) {
                    attemptStartAuth();
                    return true;
                }
                return false;
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptStartAuth();
            }
        });

        switch (mAuthMode) {
            case AUTH_MODE_NEW:
                setTitle(R.string.auth_title_new);
                break;
            case AUTH_MODE_ADD:
                setTitle(R.string.auth_title_add);
                break;
            case AUTH_MODE_UPDATE:
                setTitle(R.string.auth_title_update);
                // See the source of setKeyListener(null), it just satisfies our need.
                mUsernameEdit.setKeyListener(null);
                mPasswordEdit.requestFocus();
                break;
            case AUTH_MODE_CONFIRM:
                setTitle(R.string.auth_title_confirm);
                mUsernameEdit.setKeyListener(null);
                mPasswordEdit.requestFocus();
                break;
            default:
                throw new IllegalArgumentException();
        }

        if (savedInstanceState != null) {
            mUsernameLayout.setError(savedInstanceState.getCharSequence(
                    STATE_KEY_USERNAME_LAYOUT_ERROR));
            mPasswordLayout.setError(savedInstanceState.getCharSequence(
                    STATE_KEY_PASSWORD_LAYOUT_ERROR));
        }

        // View only saves state influenced by user action, so we have to do this ourselves.
        ViewState viewState = mRetainDataFragment.remove(RETAIN_DATA_KEY_VIEW_STATE);
        if (viewState != null) {
            onRestoreViewState(viewState);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharSequence(STATE_KEY_USERNAME_LAYOUT_ERROR, mUsernameLayout.getError());
        outState.putCharSequence(STATE_KEY_PASSWORD_LAYOUT_ERROR, mPasswordLayout.getError());

        mRetainDataFragment.put(RETAIN_DATA_KEY_VIEW_STATE, onSaveViewState());
    }

    private ViewState onSaveViewState() {
        return new ViewState(mLoginButton.isEnabled(), mShowingProgress);
    }

    private void onRestoreViewState(ViewState state) {
        mLoginButton.setEnabled(state.loginButtonEnabled);
        mShowingProgress = state.showingProgress;
        ViewUtils.setVisibleOrInvisible(mFormLayout, !mShowingProgress);
        ViewUtils.setVisibleOrInvisible(mProgress, mShowingProgress);
    }

    @Override
    public void onVolleyResponse(int requestCode, boolean successful, TokenRequest.Result result,
                                 VolleyError error, Void requestState) {
        if (requestCode == REQUEST_CODE_AUTH) {
            onAuthResponse(successful, result, error);
        } else {
            LogUtils.w("Unknown request code " + requestCode + ", with successful=" + successful
                    + ", result=" + result + ", error=" + error);
        }
    }

    private void attemptStartAuth() {

        // Store values at the time of login attempt.
        mUsername = mUsernameEdit.getText().toString();
        mPassword = mPasswordEdit.getText().toString();

        boolean cancel = false;
        View errorView = null;

        if (TextUtils.isEmpty(mUsername)) {
            mUsernameLayout.setError(getString(R.string.auth_error_empty_username));
            errorView = mUsernameEdit;
            cancel = true;
        }
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordLayout.setError(getString(R.string.auth_error_empty_password));
            if (errorView == null) {
                errorView = mPasswordEdit;
            }
            cancel = true;
        }

        if (cancel) {
            errorView.requestFocus();
        } else {
            onStartAuth();
        }
    }

    private void onStartAuth() {

        TokenRequest request = new TokenRequest(mUsername, mPassword);
        RequestFragment.startRequest(request, null, this, REQUEST_CODE_AUTH);

        mUsernameLayout.setError(null);
        mPasswordLayout.setError(null);
        mLoginButton.setEnabled(false);
        ViewUtils.crossfade(mFormLayout, mProgress, false);
        mShowingProgress = true;
    }

    private void onAuthResponse(boolean successful, TokenRequest.Result result, VolleyError error) {
        if (successful) {
            onAuthResult(result);
        } else {
            onAuthError(error);
        }
    }

    private void onAuthResult(TokenRequest.Result result) {

        Account account = new Account(mUsername, AccountContract.ACCOUNT_TYPE);

        switch (mAuthMode) {
            case AUTH_MODE_NEW:
                AccountUtils.addAccountExplicitly(account, mPassword, this);
                AccountUtils.setActiveAccount(account, this);
                break;
            case AUTH_MODE_ADD:
                AccountUtils.addAccountExplicitly(account, mPassword, this);
                break;
            case AUTH_MODE_UPDATE:
            case AUTH_MODE_CONFIRM:
                AccountUtils.setPassword(account, mPassword, this);
                break;
        }

        AccountUtils.setUserName(account, result.userName, this);
        AccountUtils.setUserId(account, result.userId, this);
        AccountUtils.setAuthToken(account, result.accessToken, this);
        AccountUtils.setRefreshToken(account, result.refreshToken, this);

        Intent intent;
        switch (mAuthMode) {
            case AUTH_MODE_NEW:
            case AUTH_MODE_ADD:
            case AUTH_MODE_UPDATE:
                intent = makeSuccessIntent(mUsername);
                break;
            case AUTH_MODE_CONFIRM:
                intent = makeBooleanIntent(true);
                break;
            default:
                throw new IllegalArgumentException();
        }
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void onAuthError(VolleyError error) {

        LogUtils.e(error.toString());
        if (error instanceof ParseError) {
            mPasswordLayout.setError(getString(R.string.auth_error_invalid_response));
        } else if (error instanceof ApiError) {
            ApiError apiError = (ApiError) error;
            String errorString = getString(apiError.getErrorStringRes());
            switch (apiError.code) {
                case Token.NOT_TRIAL_USER:
                case Token.INVALID_USER:
                case Token.USER_HAS_BLOCKED:
                case Token.USER_LOCKED:
                    mUsernameLayout.setError(errorString);
                    break;
                default:
                    mPasswordLayout.setError(errorString);
                    break;
            }
        } else {
            mPasswordLayout.setError(getString(R.string.auth_error_unknown));
        }

        mLoginButton.setEnabled(true);
        ViewUtils.crossfade(mProgress, mFormLayout);
        mShowingProgress = false;
    }

    private static Intent makeSuccessIntent(String accountName) {
        Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, accountName);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, AccountContract.ACCOUNT_TYPE);
        return intent;
    }

    private static Intent makeBooleanIntent(boolean result) {
        Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_BOOLEAN_RESULT, result);
        return intent;
    }

    private static class ViewState {

        public boolean loginButtonEnabled;
        public boolean showingProgress;

        public ViewState(boolean loginButtonEnabled, boolean showingProgress) {
            this.loginButtonEnabled = loginButtonEnabled;
            this.showingProgress = showingProgress;
        }
    }
}
