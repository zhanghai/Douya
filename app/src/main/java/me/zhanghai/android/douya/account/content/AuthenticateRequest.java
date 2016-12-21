/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.account.content;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.android.volley.VolleyError;

import me.zhanghai.android.douya.network.Request;
import me.zhanghai.android.douya.network.RequestFragment;
import me.zhanghai.android.douya.network.api.TokenRequest;
import me.zhanghai.android.douya.network.api.TokenRequests;
import me.zhanghai.android.douya.util.FragmentUtils;

public class AuthenticateRequest extends RequestFragment<AuthenticateRequest.RequestState,
        TokenRequest.Response> {

    private static final String FRAGMENT_TAG_DEFAULT = AuthenticateRequest.class.getName();

    public static AuthenticateRequest attachTo(Fragment fragment, String tag, int requestCode) {
        FragmentActivity activity = fragment.getActivity();
        AuthenticateRequest instance = FragmentUtils.findByTag(activity, tag);
        if (instance == null) {
            //noinspection deprecation
            instance = new AuthenticateRequest();
            instance.targetAt(fragment, requestCode);
            FragmentUtils.add(instance, activity, tag);
        }
        return instance;
    }

    public static AuthenticateRequest attachTo(Fragment fragment) {
        return attachTo(fragment, FRAGMENT_TAG_DEFAULT, REQUEST_CODE_INVALID);
    }

    /**
     * @deprecated Use {@code attachTo()} instead.
     */
    public AuthenticateRequest() {}

    public void start(String authTokenType, String username, String password) {
        start(new RequestState(authTokenType, username, password));
    }

    @Override
    protected Request<TokenRequest.Response> onCreateRequest(RequestState requestState) {
        return TokenRequests.newRequest(requestState.authTokenType, requestState.username,
                requestState.password);
    }

    @Override
    protected void onRequestStarted() {
        getListener().onAuthenticateStarted(getRequestCode());
    }

    @Override
    protected void onRequestFinished(boolean successful, RequestState requestState,
                                     TokenRequest.Response response, VolleyError error) {
        getListener().onAuthenticateFinished(getRequestCode());
        if (successful) {
            getListener().onAuthenticateSuccess(getRequestCode(), requestState, response);
        } else {
            getListener().onAuthenticateError(getRequestCode(), requestState, error);
        }
    }

    private Listener getListener() {
        return (Listener) getTarget();
    }

    public static class RequestState {

        public String authTokenType;
        public String username;
        public String password;

        public RequestState(String authTokenType, String username, String password) {
            this.authTokenType = authTokenType;
            this.username = username;
            this.password = password;
        }
    }

    public interface Listener {
        void onAuthenticateStarted(int requestCode);
        void onAuthenticateFinished(int requestCode);
        void onAuthenticateSuccess(int requestCode, RequestState requestState,
                                   TokenRequest.Response response);
        void onAuthenticateError(int requestCode, RequestState requestState, VolleyError error);
    }
}
