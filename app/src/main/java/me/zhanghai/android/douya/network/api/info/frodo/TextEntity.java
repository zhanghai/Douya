/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Patterns;
import android.webkit.URLUtil;

import java.util.List;

import me.zhanghai.android.douya.settings.info.Settings;
import me.zhanghai.android.douya.ui.UriSpan;
import me.zhanghai.android.douya.util.LogUtils;

public class TextEntity implements Parcelable {

    public int end;

    public int start;

    public String title;

    public String uri;

    public static CharSequence applyEntities(String text, List<TextEntity> entityList) {

        if (TextUtils.isEmpty(text) || entityList.isEmpty()) {
            return text;
        }

        SpannableStringBuilder builder = new SpannableStringBuilder();
        int lastTextIndex = 0;
        for (TextEntity entity : entityList) {
            if (entity.start < 0 || entity.start >= text.length() || entity.end < entity.start) {
                LogUtils.w("Ignoring malformed entity " + entity);
                continue;
            }
            if (entity.start < lastTextIndex) {
                LogUtils.w("Ignoring backward entity " + entity + ", with lastTextIndex="
                        + lastTextIndex);
                continue;
            }
            builder.append(text.substring(lastTextIndex, entity.start));
            String entityText = entity.title;
            if (Settings.SHOW_LONG_URL_FOR_LINK_ENTITY.getValue()
                    && URLUtil.isNetworkUrl(entityText)
                    && Patterns.WEB_URL.matcher(entityText).matches()) {
                entityText = entity.uri;
            }
            int entityStartInAppliedText = builder.length();
            builder
                    .append(entityText)
                    .setSpan(new UriSpan(entity.uri), entityStartInAppliedText,
                            entityStartInAppliedText + entityText.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            lastTextIndex = entity.end;
        }
        if (lastTextIndex != text.length()) {
            builder.append(text.substring(lastTextIndex, text.length()));
        }
        return builder;
    }


    public static final Creator<TextEntity> CREATOR = new Creator<TextEntity>() {
        public TextEntity createFromParcel(Parcel source) {
            return new TextEntity(source);
        }
        public TextEntity[] newArray(int size) {
            return new TextEntity[size];
        }
    };

    public TextEntity() {}

    protected TextEntity(Parcel in) {
        end = in.readInt();
        start = in.readInt();
        title = in.readString();
        uri = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(end);
        dest.writeInt(start);
        dest.writeString(title);
        dest.writeString(uri);
    }
}
