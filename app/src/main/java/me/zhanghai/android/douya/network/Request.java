/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * {@inheritDoc}
 */
public abstract class Request<T> extends com.android.volley.Request<T> {

    private Response.Listener<T> mListener;
    private Response.ErrorListener mErrorListener;
    // Take over control of url from super class.
    private final String mUrl;
    private String mRedirectUrl;
    private Map<String, String> mHeaders = new HashMap<>();
    private Map<String, String> mParams = new HashMap<>();
    private String mParamsEncoding = Http.Charsets.UTF8;
    private String mContentType = Http.ContentTypes.FORM_UTF8;
    private Priority mPriority = Priority.NORMAL;

    /**
     * By directly setting listeners in constructor you cannot handle Activity recreation correctly.
     * Use {@link RequestFragment} instead.
     */
    public Request(int method, String url) {
        super(method, null, null);

        mUrl = url;
    }

    public Response.Listener<T> getListener() {
        return mListener;
    }

    public Request<T> setListener(Response.Listener<T> listener) {
        mListener = listener;
        return this;
    }

    @Override
    public Response.ErrorListener getErrorListener() {
        return mErrorListener;
    }

    public Request<T> setErrorListener(Response.ErrorListener errorListener) {
        mErrorListener = errorListener;
        return this;
    }

    @Override
    protected void deliverResponse(T response) {
        if (mListener != null) {
            mListener.onResponse(response);
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        if (mErrorListener != null) {
            mErrorListener.onErrorResponse(error);
        }
    }

    @Override
    public String getUrl() {
        if (mRedirectUrl != null) {
            return mRedirectUrl;
        } else if (getMethod() == Method.GET && !mParams.isEmpty()) {
            return getUrlWithParams();
        } else {
            return mUrl;
        }
    }

    @Override
    public String getOriginUrl() {
        return mUrl;
    }

    @Override
    public String getCacheKey() {
        return getMethod() + ":" + mUrl;
    }

    public String getRedirectUrl() {
        return mRedirectUrl;
    }

    @Override
    public void setRedirectUrl(String redirectUrl) {
        mRedirectUrl = redirectUrl;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public Request<T> addHeader(String name, String value) {
        mHeaders.put(name, value);
        return this;
    }

    public Request<T> addHeader(Map.Entry<String, String> header) {
        return addHeader(header.getKey(), header.getValue());
    }

    public Request<T> addHeaderAccept(String contentType) {
        return addHeader(Http.Headers.ACCEPT, contentType);
    }

    public Request<T> addHeaderAcceptWithCharset(String contentType, String charset) {
        return addHeaderAccept(Http.ContentTypes.withCharset(contentType, charset));
    }

    public Request<T> addHeaderAcceptJson(String charset) {
        return addHeaderAcceptWithCharset(Http.ContentTypes.JSON, charset);
    }

    public Request<T> addHeaderAcceptJsonUtf8() {
        return addHeaderAcceptJson(Http.Charsets.UTF8);
    }

    public Request<T> addHeaderAcceptCharset(String charset) {
        return addHeader(Http.Headers.ACCEPT_CHARSET, charset);
    }

    public Request<T> addHeaderAcceptCharsetUtf8() {
        return addHeaderAcceptCharset(Http.Charsets.UTF8);
    }

    public Request<T> addHeaderAcceptEncoding(String encoding) {
        return addHeader(Http.Headers.ACCEPT_ENCODING, encoding);
    }

    /**
     * @deprecated Underlying network stack automatically adds accept gzip and handles compressed
     * response. If we do this explicitly we need to do the decompression ourselves.
     */
    public Request<T> addHeaderAcceptEncodingGzip() {
        return addHeaderAcceptEncoding(Http.Encodings.GZIP);
    }

    public Request<T> addHeaderAuthorization(String authorization) {
        return addHeader(Http.Headers.AUTHORIZATION, authorization);
    }

    public Request<T> addHeaderAuthorizationBearer(String token) {
        return addHeaderAuthorization(Http.Headers.makeBearerAuthorization(token));
    }

    public Request<T> addHeaderUserAgent(String userAgent) {
        return addHeader(Http.Headers.USER_AGENT, userAgent);
    }

    public Request<T> addHeaders(Map<String, String> headers) {
        mHeaders.putAll(headers);
        return this;
    }

    public Request<T> removeHeader(String name) {
        mHeaders.remove(name);
        return this;
    }

    public Request<T> clearHeaders() {
        mHeaders.clear();
        return this;
    }

    public Request<T> setHeaders(Map<String, String> headers) {
        return clearHeaders().addHeaders(headers);
    }

    @Override
    public Map<String, String> getParams() {
        return mParams;
    }

    public Request<T> addParam(String name, String value) {
        mParams.put(name, value);
        return this;
    }

    public Request<T> addParam(Map.Entry<String, String> param) {
        return addParam(param.getKey(), param.getValue());
    }

    public Request<T> addParams(Map<String, String> params) {
        mParams.putAll(params);
        return this;
    }

    public Request<T> removeParam(String name) {
        mParams.remove(name);
        return this;
    }

    public Request<T> clearParams() {
        mParams.clear();
        return this;
    }

    public Request<T> setParams(Map<String, String> params) {
        return clearParams().addParams(params);
    }

    @Override
    public String getParamsEncoding() {
        return mParamsEncoding;
    }

    public void setParamsEncoding(String paramsEncoding) {
        mParamsEncoding = paramsEncoding;
    }

    public String getContentType() {
        return mContentType;
    }

    public Request<T> setContentType(String contentType) {
        mContentType = contentType;
        return this;
    }

    @Override
    public String getBodyContentType() {
        return getContentType();
    }

    @Override
    public byte[] getBody() {
        int method = getMethod();
        if ((method == Method.POST || method == Method.PUT) && !mParams.isEmpty()) {
            return encodeParams();
        }
        return null;
    }

    @Override
    public Priority getPriority() {
        return mPriority;
    }

    public void setPriority(Priority priority) {
        mPriority = priority;
    }

    public void onPreparePerformRequest() throws AuthFailureError {}

    public T getResponse() throws InterruptedException, TimeoutException, ExecutionException {
        RequestFuture<T> future = RequestFuture.newFuture();
        setListener(future);
        setErrorListener(future);
        Volley.getInstance().addToRequestQueue(this);
        return future.get(getTimeoutMs(), TimeUnit.MILLISECONDS);
    }

    private StringBuilder appendEncodedParams(StringBuilder builder) {
        for (Map.Entry<String, String> entry : mParams.entrySet()) {
            try {
                builder
                        // FIXME: URLEncoder is in fact conforming to
                        // application/x-www-form-urlencoded, instead of encoding the string as a
                        // URL.
                        .append(URLEncoder.encode(entry.getKey(), mParamsEncoding))
                        .append('=')
                        .append(URLEncoder.encode(entry.getValue(), mParamsEncoding))
                        .append('&');
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return builder;
    }

    // TODO: Cache this?
    private String getUrlWithParams() {
        StringBuilder builder = new StringBuilder(mUrl)
                .append('?');
        return appendEncodedParams(builder)
                .toString();
    }

    private byte[] encodeParams() {
        try {
            return appendEncodedParams(new StringBuilder())
                    .toString()
                    .getBytes(mParamsEncoding);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
