/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.settings.info;

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

    BooleanSettingsEntry AUTO_REFRESH_HOME = new BooleanSettingsEntry(
            R.string.pref_key_auto_refresh_home, R.bool.pref_default_value_auto_refresh_home);

    BooleanSettingsEntry SHOW_TITLE_FOR_LINK_ENTITY = new BooleanSettingsEntry(
            R.string.pref_key_show_title_for_link_entity,
            R.bool.pref_default_value_show_title_for_link_entity);

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

    BooleanSettingsEntry REQUEST_DESKTOP_SITE_IN_WEBVIEW = new BooleanSettingsEntry(
            R.string.pref_key_request_desktop_site_in_webview,
            R.bool.pref_default_value_request_desktop_site_in_webview);

    BooleanSettingsEntry ALWAYS_COPY_TO_CLIPBOARD_AS_TEXT = new BooleanSettingsEntry(
            R.string.pref_key_always_copy_to_clipboard_as_text,
            R.bool.pref_default_value_always_copy_to_clipboard_as_text);
}
