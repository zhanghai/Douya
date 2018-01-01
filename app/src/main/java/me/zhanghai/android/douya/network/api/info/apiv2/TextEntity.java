/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.apiv2;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Patterns;
import android.webkit.URLUtil;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.douya.settings.info.Settings;
import me.zhanghai.android.douya.ui.UriSpan;
import me.zhanghai.android.douya.util.LogUtils;

public class TextEntity implements Parcelable {

    public int end;

    public String href;

    public int start;

    public String title;

    public static CharSequence applyEntities(String text, List<TextEntity> entityList) {

        if (TextUtils.isEmpty(text) || entityList.isEmpty()) {
            return text;
        }

        SpannableStringBuilder builder = new SpannableStringBuilder();
        int lastTextIndex = 0;
        for (TextEntity entity : entityList) {
            if (entity.start < 0 || entity.end < entity.start) {
                LogUtils.w("Ignoring malformed entity " + entity);
                continue;
            }
            if (entity.start < lastTextIndex) {
                LogUtils.w("Ignoring backward entity " + entity + ", with lastTextIndex="
                        + lastTextIndex);
                continue;
            }
            int entityStart = text.offsetByCodePoints(lastTextIndex, entity.start - lastTextIndex);
            int entityEnd = text.offsetByCodePoints(entityStart, entity.end - entity.start);
            builder.append(text.substring(lastTextIndex, entityStart));
            String entityText = entity.title;
            if (!Settings.SHOW_LONG_URL_FOR_LINK_ENTITY.getValue()
                    && URLUtil.isNetworkUrl(entityText)
                    && Patterns.WEB_URL.matcher(entityText).matches()) {
                entityText = text.substring(entityStart, entityEnd);
            }
            int entityStartInAppliedText = builder.length();
            builder
                    .append(entityText)
                    .setSpan(new UriSpan(entity.href), entityStartInAppliedText,
                            entityStartInAppliedText + entityText.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            lastTextIndex = entityEnd;
        }
        if (lastTextIndex != text.length()) {
            builder.append(text.substring(lastTextIndex, text.length()));
        }
        return builder;
    }


    public me.zhanghai.android.douya.network.api.info.frodo.TextEntity toFrodo() {
        me.zhanghai.android.douya.network.api.info.frodo.TextEntity entity =
                new me.zhanghai.android.douya.network.api.info.frodo.TextEntity();
        entity.end = end;
        entity.start = start;
        entity.title = title;
        entity.uri = href;
        return entity;
    }

    public static ArrayList<me.zhanghai.android.douya.network.api.info.frodo.TextEntity> toFrodo(
            ArrayList<TextEntity> entities) {
        ArrayList<me.zhanghai.android.douya.network.api.info.frodo.TextEntity> frodoEntities =
                new ArrayList<>();
        for (TextEntity entity : entities) {
            frodoEntities.add(entity.toFrodo());
        }
        return frodoEntities;
    }


    public static final Parcelable.Creator<TextEntity> CREATOR = new Parcelable.Creator<TextEntity>() {
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
        href = in.readString();
        start = in.readInt();
        title = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(end);
        dest.writeString(href);
        dest.writeInt(start);
        dest.writeString(title);
    }
}
