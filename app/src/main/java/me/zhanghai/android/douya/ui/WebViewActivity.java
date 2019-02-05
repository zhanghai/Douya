/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.ui;

import android.Manifest;
import android.accounts.Account;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.account.info.AccountContract;
import me.zhanghai.android.douya.account.util.AccountUtils;
import me.zhanghai.android.douya.link.DoubanUriHandler;
import me.zhanghai.android.douya.link.FrodoBridge;
import me.zhanghai.android.douya.network.Http;
import me.zhanghai.android.douya.network.api.credential.ApiCredential;
import me.zhanghai.android.douya.network.api.info.UrlGettable;
import me.zhanghai.android.douya.settings.info.Settings;
import me.zhanghai.android.douya.util.AppUtils;
import me.zhanghai.android.douya.util.ArrayUtils;
import me.zhanghai.android.douya.util.ClipboardUtils;
import me.zhanghai.android.douya.util.CollectionUtils;
import me.zhanghai.android.douya.util.NightModeHelper;
import me.zhanghai.android.douya.util.ShareUtils;
import me.zhanghai.android.douya.util.StringUtils;
import me.zhanghai.android.douya.util.ToastUtils;
import me.zhanghai.android.douya.util.UriUtils;
import me.zhanghai.android.douya.util.UrlUtils;
import me.zhanghai.android.douya.util.ViewUtils;
import me.zhanghai.android.douya.util.WebViewUtils;
import me.zhanghai.android.effortlesspermissions.AfterPermissionDenied;
import me.zhanghai.android.effortlesspermissions.EffortlessPermissions;
import me.zhanghai.android.effortlesspermissions.OpenAppDetailsDialogFragment;
import pub.devrel.easypermissions.AfterPermissionGranted;

public class WebViewActivity extends AppCompatActivity {

    private static final String KEY_PREFIX = WebViewActivity.class.getName() + '.';

    private static final String EXTRA_DISABLE_LOAD_OVERRIDING_URLS = KEY_PREFIX
            + "disable_load_overriding_urls";

    private static final int REQUEST_CODE_FILE_CHOOSER = 1;

    private static final int REQUEST_CODE_DOWNLOAD_PERMISSION = 2;
    private static final String[] PERMISSIONS_DOWNLOAD = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final Pattern DOUBAN_HOST_PATTERN = Pattern.compile(".*\\.douban\\.(com|fm)");

    private static final String DOUBAN_OAUTH2_REDIRECT_URL_FORMAT =
            "https://www.douban.com/accounts/auth2_redir?url=%1$s&apikey=%2$s";

    @BindDimen(R.dimen.toolbar_height)
    int mToolbarHeight;

    @BindView(R.id.appBarWrapper)
    AppBarWrapperLayout mAppbarWrapperLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_progress)
    ProgressBar mProgress;
    @BindView(R.id.web)
    WebView mWebView;

    private MenuItem mGoForwardMenuItem;
    private MenuItem mOpenWithNativeMenuItem;
    private MenuItem mRequestDesktopSiteMenuItem;

    private Set<String> mDisableLoadOverridingUrls;

    private ValueCallback<Uri> mUploadFile;
    private ValueCallback<Uri[]> mFilePathCallback;

    private DownloadInfo mDownloadInfo;

    private String mTitleOrError;
    private boolean mProgressVisible;
    private String mDefaultUserAgent;
    private String mDesktopUserAgent;

    public static Intent makeIntent(Uri uri, Context context) {
        Intent intent = new Intent(context, WebViewActivity.class)
                .setData(uri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Settings.CREATE_NEW_TASK_FOR_WEBVIEW.getValue()) {
                intent
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
                    .addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            }
        }
        return intent;
    }

    /**
     * @deprecated You probably want to use {@link #makeIntent(String, boolean, Context)} instead.
     */
    public static Intent makeIntent(String uri, Context context) {
        return makeIntent(Uri.parse(uri), context);
    }

    public static Intent makeIntent(String uri, String[] disableLoadOverridingUrls,
                                    Context context) {
        //noinspection deprecation
        return makeIntent(uri, context)
                .putExtra(EXTRA_DISABLE_LOAD_OVERRIDING_URLS, disableLoadOverridingUrls);
    }

    public static Intent makeIntent(String uri, String disableLoadOverridingUrl, Context context) {
        return makeIntent(uri, new String[] { disableLoadOverridingUrl }, context);
    }

    public static Intent makeIntent(String uri, boolean disableLoadOverriding, Context context) {
        if (!disableLoadOverriding) {
            throw new IllegalArgumentException("disableLoadOverriding should always be true");
        }
        return makeIntent(uri, new String[] { uri }, context);
    }

    public static Intent makeIntent(UrlGettable urlGettable, Context context) {
        return makeIntent(urlGettable.getUrl(), true, context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] disableLoadOverridingUrls = getIntent().getStringArrayExtra(
                EXTRA_DISABLE_LOAD_OVERRIDING_URLS);
        mDisableLoadOverridingUrls = new HashSet<>();
        if (disableLoadOverridingUrls != null) {
            mDisableLoadOverridingUrls.addAll(Arrays.asList(disableLoadOverridingUrls));
        }

        setContentView(R.layout.webview_activity);
        ButterKnife.bind(this);

        setupToolbar();
        setupWebView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ((ViewGroup) mWebView.getParent()).removeView(mWebView);
        mWebView.destroy();
        mWebView = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        // Need to do this before calling super to avoid activity recreation by AppCompat.
        NightModeHelper.onConfigurationChanged(this, this::onApplyThemeResource,
                R.style.Theme_Douya);

        super.onConfigurationChanged(newConfig);

        Context themedContext = new ContextThemeWrapper(this, ViewUtils.getResIdFromAttrRes(
                R.attr.actionBarTheme, 0, this));
        Toolbar newToolbar = (Toolbar) LayoutInflater.from(themedContext).inflate(
                R.layout.webview_acitivity_toolbar, mAppbarWrapperLayout, false);
        ViewUtils.replaceChild(mAppbarWrapperLayout, mToolbar, newToolbar);
        ButterKnife.bind(this);

        setupToolbar();
        ViewUtils.setVisibleOrGone(mProgress, mProgressVisible);
        ViewUtils.setMarginTop(mWebView, mToolbarHeight);
        mWebView.requestLayout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.webview, menu);
        mGoForwardMenuItem = menu.findItem(R.id.action_go_forward);
        updateGoForward();
        mOpenWithNativeMenuItem = menu.findItem(R.id.action_open_with_native);
        updateOpenWithNative();
        mRequestDesktopSiteMenuItem = menu.findItem(R.id.action_request_desktop_site);
        updateRequestDesktopSite();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_go_forward:
                goForward();
                return true;
            case R.id.action_reload:
                reloadWebView();
                return true;
            case R.id.action_copy_url:
                copyUrl();
                return true;
            case R.id.action_share:
                share();
                return true;
            case R.id.action_open_with_native:
                toggleOpenWithNative();
                return true;
            case R.id.action_request_desktop_site:
                toggleRequestDesktopSite();
                return true;
            case R.id.action_open_in_browser:
                openInBrowser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EffortlessPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults,
                this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_FILE_CHOOSER:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (mFilePathCallback != null) {
                        // This cannot handle multiple URIs.
                        //Uri[] value = WebChromeClient.FileChooserParams.parseResult(resultCode,
                        //        data);
                        Uri[] value = WebViewUtils.parseFileChooserResult(resultCode, data);
                        mFilePathCallback.onReceiveValue(value);
                        mFilePathCallback = null;
                    }
                } else {
                    if (mUploadFile != null) {
                        Uri[] value = WebViewUtils.parseFileChooserResult(resultCode, data);
                        mUploadFile.onReceiveValue(!ArrayUtils.isEmpty(value) ? value[0] : null);
                        mUploadFile = null;
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected void onLoadUri(WebView webView) {
        String url = getIntent().getData().toString();
        if (Settings.REQUEST_DESKTOP_SITE_IN_WEBVIEW.getValue()) {
            url = getDoubanDesktopSiteUrl(url);
        }
        Map<String, String> headers = null;
        if (isDoubanUrl(url)) {
            Account account = AccountUtils.getActiveAccount();
            if (account != null) {
                String authToken = AccountUtils.peekAuthToken(account,
                        AccountContract.AUTH_TOKEN_TYPE_FRODO);
                if (!TextUtils.isEmpty(authToken)) {
                    url = StringUtils.formatUs(DOUBAN_OAUTH2_REDIRECT_URL_FORMAT, Uri.encode(url),
                            Uri.encode(ApiCredential.ApiV2.KEY));
                    headers = new HashMap<>();
                    headers.put(Http.Headers.AUTHORIZATION,
                            Http.Headers.makeBearerAuthorization(authToken));
                }
            }
        }
        webView.loadUrl(url, headers);
    }

    private boolean isDoubanUrl(String url) {
        return DOUBAN_HOST_PATTERN.matcher(Uri.parse(url).getHost()).matches();
    }

    protected void onPageStared(WebView webView, String url, Bitmap favicon) {
        updateGoForward();
        updateToolbarTitleAndSubtitle();
    }

    protected void onPageFinished(WebView webView, String url) {}

    protected boolean shouldOverrideUrlLoading(WebView webView, String url) {
        Uri uri = Uri.parse(url);
        if (mDisableLoadOverridingUrls.contains(UriUtils.withoutQueryAndFragment(uri).toString())) {
            return false;
        }
        return (Settings.OPEN_WITH_NATIVE_IN_WEBVIEW.getValue() && DoubanUriHandler.open(uri, this))
                || FrodoBridge.openFrodoUri(uri, this)
                || (Settings.PROGRESSIVE_THIRD_PARTY_APP.getValue()
                    && FrodoBridge.openUri(uri, this));
    }

    protected void reloadWebView() {
        mWebView.reload();
    }

    private void setupToolbar() {
        if (Settings.CREATE_NEW_TASK_FOR_WEBVIEW.getValue()) {
            mToolbar.setNavigationIcon(R.drawable.close_icon_white_24dp);
        }
        setSupportActionBar(mToolbar);
        updateToolbarTitleAndSubtitle();
    }

    private void updateToolbarTitleAndSubtitle() {
        String url = mWebView.getUrl();
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (TextUtils.isEmpty(mTitleOrError)) {
            mTitleOrError = Uri.parse(url).getHost();
        }
        ActionBar actionBar = getSupportActionBar();
        if (TextUtils.isEmpty(actionBar.getSubtitle())) {
            mToolbar.setTitleTextAppearance(mToolbar.getContext(),
                    R.style.TextAppearance_Widget_Douya_Toolbar_Title_WebView);
            mToolbar.setSubtitleTextAppearance(mToolbar.getContext(),
                    R.style.TextAppearance_Widget_Douya_Toolbar_Subtitle_WebView);
        }
        setTitle(mTitleOrError);
        if (Settings.CREATE_NEW_TASK_FOR_WEBVIEW.getValue()) {
            AppUtils.setTaskDescriptionLabel(this, mTitleOrError);
        }
        actionBar.setSubtitle(url);
    }

    private void setProgressVisible(boolean visible) {
        if (mProgressVisible != visible) {
            mProgressVisible = visible;
            ViewUtils.setVisibleOrGone(mProgress, visible);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            webSettings.setDatabasePath(WebViewUtils.getDatabasePath(this));
        }
        webSettings.setDatabaseEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        webSettings.setJavaScriptEnabled(true);
        initializeUserAgents();
        updateUserAgent();
        // NOTE: This gives double tap zooming.
        webSettings.setUseWideViewPort(true);
        mWebView.setWebChromeClient(new ChromeClient());
        mWebView.setWebViewClient(new ViewClient());
        mWebView.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength)
                -> download(url, userAgent, contentDisposition, mimeType));
        onLoadUri(mWebView);
    }

    private void goForward() {
        mWebView.goForward();
        // Handled in onPageStared().
        //updateGoForward();
    }

    private void updateGoForward() {
        if (mGoForwardMenuItem == null) {
            return;
        }
        mGoForwardMenuItem.setEnabled(mWebView.canGoForward());
    }

    private void copyUrl() {
        String url = mWebView.getUrl();
        if (TextUtils.isEmpty(url)) {
            ToastUtils.show(R.string.webview_error_url_empty, this);
            return;
        }
        ClipboardUtils.copyText(mWebView.getTitle(), url, this);
    }

    private void share() {
        String url = mWebView.getUrl();
        if (TextUtils.isEmpty(url)) {
            ToastUtils.show(R.string.webview_error_url_empty, this);
            return;
        }
        ShareUtils.shareText(url, this);
    }

    private void toggleOpenWithNative() {
        Settings.OPEN_WITH_NATIVE_IN_WEBVIEW.putValue(
                !Settings.OPEN_WITH_NATIVE_IN_WEBVIEW.getValue());
        updateOpenWithNative();
    }

    private void updateOpenWithNative() {
        if (mOpenWithNativeMenuItem == null) {
            return;
        }
        mOpenWithNativeMenuItem.setChecked(Settings.OPEN_WITH_NATIVE_IN_WEBVIEW.getValue());
    }

    private void toggleRequestDesktopSite() {
        Settings.REQUEST_DESKTOP_SITE_IN_WEBVIEW.putValue(
                !Settings.REQUEST_DESKTOP_SITE_IN_WEBVIEW.getValue());
        updateRequestDesktopSite();
        updateUserAgent();
    }

    private void updateRequestDesktopSite() {
        if (mRequestDesktopSiteMenuItem == null) {
            return;
        }
        mRequestDesktopSiteMenuItem.setChecked(Settings.REQUEST_DESKTOP_SITE_IN_WEBVIEW.getValue());
    }

    private void initializeUserAgents() {
        mDefaultUserAgent = mWebView.getSettings().getUserAgentString();
        mDesktopUserAgent = mDefaultUserAgent
                .replaceFirst("\\(Linux;.*?\\)", "(X11; Linux x86_64)")
                .replace("Mobile Safari/", "Safari/");
    }

    private void updateUserAgent() {
        boolean requestDesktopSite = Settings.REQUEST_DESKTOP_SITE_IN_WEBVIEW.getValue();
        WebSettings webSettings = mWebView.getSettings();
        String oldUserAgent = webSettings.getUserAgentString();
        boolean changed = false;
        if (requestDesktopSite && !TextUtils.equals(oldUserAgent, mDesktopUserAgent)) {
            webSettings.setUserAgentString(mDesktopUserAgent);
            changed = true;
        } else if (!requestDesktopSite && !TextUtils.equals(oldUserAgent, mDefaultUserAgent)) {
            // This will requrie API level 17.
            //webSettings.setUserAgentString(WebSettings.getDefaultUserAgent(this));
            webSettings.setUserAgentString(mDefaultUserAgent);
            changed = true;
        }
        String url = mWebView.getUrl();
        if (!TextUtils.isEmpty(url) && changed) {
            if (requestDesktopSite) {
                String doubanDesktopSiteUrl = getDoubanDesktopSiteUrl(url);
                if (!TextUtils.equals(url, doubanDesktopSiteUrl)) {
                    mWebView.loadUrl(doubanDesktopSiteUrl);
                } else {
                    mWebView.reload();
                }
            } else {
                mWebView.reload();
            }
        }
    }

    private String getDoubanDesktopSiteUrl(String url) {
        Uri uri = Uri.parse(url);
        if (!TextUtils.equals(uri.getHost(), "m.douban.com")
                || TextUtils.equals(CollectionUtils.firstOrNull(uri.getPathSegments()), "page")) {
            return url;
        }
        return uri.buildUpon()
                .path("/to_pc/")
                .appendQueryParameter("url", url)
                .build()
                .toString();
    }

    private void openInBrowser() {
        String url = mWebView.getUrl();
        if (!TextUtils.isEmpty(url)) {
            UrlUtils.openWithIntent(url, this);
        } else {
            ToastUtils.show(R.string.webview_error_url_empty, this);
        }
    }

    private void download(String url, String userAgent, String contentDisposition,
                          String mimeType) {
        mDownloadInfo = new DownloadInfo(url, userAgent, contentDisposition, mimeType);
        download();
    }

    @AfterPermissionGranted(REQUEST_CODE_DOWNLOAD_PERMISSION)
    private void download() {
        if (EffortlessPermissions.hasPermissions(this, PERMISSIONS_DOWNLOAD)) {
            downloadWithPermission();
        } else {
            EffortlessPermissions.requestPermissions(this,
                    R.string.webview_download_permission_request_message,
                    REQUEST_CODE_DOWNLOAD_PERMISSION, PERMISSIONS_DOWNLOAD);
        }
    }

    @AfterPermissionDenied(REQUEST_CODE_DOWNLOAD_PERMISSION)
    private void onDownloadPermissionDenied() {
        if (EffortlessPermissions.somePermissionPermanentlyDenied(this, PERMISSIONS_DOWNLOAD)) {
            OpenAppDetailsDialogFragment.show(
                    R.string.webview_download_permission_permanently_denied_message,
                    R.string.open_settings, this);
        } else {
            ToastUtils.show(R.string.webview_download_permission_denied, this);
        }
    }

    private void downloadWithPermission() {
        WebViewUtils.download(mDownloadInfo.mUrl, mDownloadInfo.mUserAgent,
                mDownloadInfo.mContentDisposition, mDownloadInfo.mMimeType, this);
        mDownloadInfo = null;
    }

    private class ChromeClient extends WebChromeClient {

        // NOTE: WebView can be trying to show an AlertDialog after the activity is finished, which
        // will result in a WindowManager$BadTokenException.
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return WebViewActivity.this.isFinishing() || super.onJsAlert(view, url, message,
                    result);
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            return WebViewActivity.this.isFinishing() || super.onJsConfirm(view, url, message,
                    result);
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
                                  JsPromptResult result) {
            return WebViewActivity.this.isFinishing() || super.onJsPrompt(view, url, message,
                    defaultValue, result);
        }

        @Override
        public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
            return WebViewActivity.this.isFinishing() || super.onJsBeforeUnload(view, url, message,
                    result);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            setProgressVisible(newProgress != 100);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            mTitleOrError = title;
            updateToolbarTitleAndSubtitle();
        }

        // For API level <= 15.
        //@Override
        @SuppressWarnings("unused")
        public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType) {
            if (mUploadFile != null) {
                mUploadFile.onReceiveValue(null);
            }
            mUploadFile = uploadFile;
            AppUtils.startActivityForResultWithChooser(WebViewUtils.createFileChooserIntent(
                    acceptType), REQUEST_CODE_FILE_CHOOSER, WebViewActivity.this);
        }

        // For 16 <= API level <= 19.
        //@Override
        @SuppressWarnings("unused")
        public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType,
                                    String capture) {
            openFileChooser(uploadFile, acceptType);
        }

        // For API level >= 21.
        @Override
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                         FileChooserParams fileChooserParams) {
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePathCallback;
            AppUtils.startActivityForResultWithChooser(WebViewUtils.createFileChooserIntent(
                    fileChooserParams), REQUEST_CODE_FILE_CHOOSER, WebViewActivity.this);
            return true;
        }
    }

    private class ViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            WebViewActivity.this.onPageStared(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            WebViewActivity.this.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description,
                                    String failingUrl) {
            mTitleOrError = description;
            updateToolbarTitleAndSubtitle();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return WebViewActivity.this.shouldOverrideUrlLoading(view, url);
        }
    }

    private static class DownloadInfo {

        public String mUrl;
        public String mUserAgent;
        public String mContentDisposition;
        public String mMimeType;

        public DownloadInfo(String url, String userAgent, String contentDisposition,
                            String mimeType) {
            mUrl = url;
            mUserAgent = userAgent;
            mContentDisposition = contentDisposition;
            mMimeType = mimeType;
        }
    }
}
