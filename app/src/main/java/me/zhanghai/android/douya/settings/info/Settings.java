/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.settings.info;

import android.support.v7.app.AppCompatDelegate;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.settings.info.SettingsEntries.*;

public interface Settings {

    StringSettingsEntry API_V2_API_KEY = new StringSettingsEntry(R.string.pref_key_api_v2_api_key,
            R.string.pref_default_value_empty_string);

    StringSettingsEntry API_V2_API_SECRET = new StringSettingsEntry(
            R.string.pref_key_api_v2_api_secret, R.string.pref_default_value_empty_string);

    StringSettingsEntry FRODO_API_KEY = new StringSettingsEntry(R.string.pref_key_frodo_api_key,
            R.string.pref_default_value_empty_string);

    StringSettingsEntry FRODO_API_SECRET = new StringSettingsEntry(
            R.string.pref_key_frodo_api_secret, R.string.pref_default_value_empty_string);

    StringSettingsEntry ACTIVE_ACCOUNT_NAME = new StringSettingsEntry(
            R.string.pref_key_active_account_name, R.string.pref_default_value_empty_string);

    StringSettingsEntry RECENT_ONE_ACCOUNT_NAME = new StringSettingsEntry(
            R.string.pref_key_recent_one_account_name, R.string.pref_default_value_empty_string);

    StringSettingsEntry RECENT_TWO_ACCOUNT_NAME = new StringSettingsEntry(
            R.string.pref_key_recent_two_account_name, R.string.pref_default_value_empty_string);

    BooleanSettingsEntry SHOW_TITLE_FOR_LINK_ENTITY = new BooleanSettingsEntry(
            R.string.pref_key_show_title_for_link_entity,
            R.bool.pref_default_value_show_title_for_link_entity);

    enum NightMode {

        // Disabled because AppCompatDelegate delegates night mode change to the non-existent system
        // implementation.
        FOLLOW_SYSTEM(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM),
        OFF(AppCompatDelegate.MODE_NIGHT_NO),
        ON(AppCompatDelegate.MODE_NIGHT_YES),
        AUTO(AppCompatDelegate.MODE_NIGHT_AUTO);

        private int value;

        NightMode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    EnumSettingsEntry<NightMode> NIGHT_MODE = new EnumSettingsEntry<>(R.string.pref_key_night_mode,
            R.string.pref_default_value_night_mode, NightMode.class);

    BooleanSettingsEntry AUTO_REFRESH_HOME = new BooleanSettingsEntry(
            R.string.pref_key_auto_refresh_home, R.bool.pref_default_value_auto_refresh_home);

    BooleanSettingsEntry PROGRESSIVE_THIRD_PARTY_APP = new BooleanSettingsEntry(
            R.string.pref_key_progressive_third_party_app,
            R.bool.pref_default_value_progressive_third_party_app);

    enum OpenUrlWithMethod {
        WEBVIEW,
        INTENT,
        CUSTOM_TABS
    }

    EnumSettingsEntry<OpenUrlWithMethod> OPEN_URL_WITH_METHOD = new EnumSettingsEntry<>(
            R.string.pref_key_open_url_with, R.string.pref_default_value_open_url_with,
            OpenUrlWithMethod.class);

    BooleanSettingsEntry OPEN_WITH_NATIVE_IN_WEBVIEW = new BooleanSettingsEntry(
            R.string.pref_key_open_with_native_in_webview,
            R.bool.pref_default_value_open_with_native_in_webview);

    BooleanSettingsEntry REQUEST_DESKTOP_SITE_IN_WEBVIEW = new BooleanSettingsEntry(
            R.string.pref_key_request_desktop_site_in_webview,
            R.bool.pref_default_value_request_desktop_site_in_webview);
}
