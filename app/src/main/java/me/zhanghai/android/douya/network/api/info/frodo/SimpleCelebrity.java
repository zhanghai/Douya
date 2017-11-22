/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SimpleCelebrity extends BaseItem {

    public Image avatar;

    public String character;

    @SerializedName("latin_name")
    public String latinName;

    public String name;

    public ArrayList<String> roles = new ArrayList<>();

    public User user;

    public transient boolean isDirector;


    public static final Creator<SimpleCelebrity> CREATOR = new Creator<SimpleCelebrity>() {
        @Override
        public SimpleCelebrity createFromParcel(Parcel source) {
            return new SimpleCelebrity(source);
        }
        @Override
        public SimpleCelebrity[] newArray(int size) {
            return new SimpleCelebrity[size];
        }
    };

    public SimpleCelebrity() {}

    protected SimpleCelebrity(Parcel in) {
        super(in);

        avatar = in.readParcelable(Image.class.getClassLoader());
        character = in.readString();
        latinName = in.readString();
        name = in.readString();
        roles = in.createStringArrayList();
        user = in.readParcelable(User.class.getClassLoader());
        isDirector = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeParcelable(avatar, flags);
        dest.writeString(character);
        dest.writeString(latinName);
        dest.writeString(name);
        dest.writeStringList(roles);
        dest.writeParcelable(user, flags);
        dest.writeByte(isDirector ? (byte) 1 : (byte) 0);
    }
}
