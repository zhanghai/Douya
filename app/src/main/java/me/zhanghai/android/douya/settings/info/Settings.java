/*
 * Copyright (c) 2015 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.settings.info;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.settings.info.SettingsEntries.*;

public class Settings {

    public static final StringSettingsEntry ACTIVE_ACCOUNT_NAME = new StringSettingsEntry(
            R.string.pref_key_active_account_name, R.string.pref_default_value_active_account_name);

    public static final BooleanSettingsEntry AUTO_REFRESH_HOME = new BooleanSettingsEntry(
            R.string.pref_key_auto_refresh_home, R.bool.pref_default_value_auto_refresh_home);

    public static final BooleanSettingsEntry SHOW_TITLE_FOR_LINK_ENTITY = new BooleanSettingsEntry(
            R.string.pref_key_show_title_for_link_entity,
            R.bool.pref_default_value_show_title_for_link_entity);

    public enum OpenUrlWithMethod {
        WEBVIEW,
        INTENT,
        CUSTOM_TABS
    }

    public static final EnumSettingsEntry<OpenUrlWithMethod> OPEN_URL_WITH_METHOD =
            new EnumSettingsEntry<>(R.string.pref_key_open_url_with,
                    R.string.pref_default_value_open_url_with, OpenUrlWithMethod.class);

    public static final BooleanSettingsEntry ALWAYS_COPY_TO_CLIPBOARD_AS_TEXT =
            new BooleanSettingsEntry(R.string.pref_key_always_copy_to_clipboard_as_text,
                    R.bool.pref_default_value_always_copy_to_clipboard_as_text);
}
