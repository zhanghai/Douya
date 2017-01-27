/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Celebrity extends BaseItem {

    @SerializedName("abstract")
    public String introduction;

    public Image avatar;

    @SerializedName("cover_url")
    public String coverUrl;

    public String name;

    public List<String> roles = new ArrayList<>();


    public static final Creator<Celebrity> CREATOR = new Creator<Celebrity>() {
        @Override
        public Celebrity createFromParcel(Parcel source) {
            return new Celebrity(source);
        }
        @Override
        public Celebrity[] newArray(int size) {
            return new Celebrity[size];
        }
    };

    public Celebrity() {}

    protected Celebrity(Parcel in) {
        super(in);

        introduction = in.readString();
        avatar = in.readParcelable(Image.class.getClassLoader());
        coverUrl = in.readString();
        name = in.readString();
        roles = in.createStringArrayList();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeString(introduction);
        dest.writeParcelable(avatar, flags);
        dest.writeString(coverUrl);
        dest.writeString(name);
        dest.writeStringList(roles);
    }
}
