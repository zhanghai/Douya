/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class UserList extends BaseList<SimpleUser> implements Parcelable {

    public ArrayList<SimpleUser> users = new ArrayList<>();

    @Override
    public ArrayList<SimpleUser> getList() {
        return users;
    }


    public static final Creator<UserList> CREATOR = new Creator<UserList>() {
        @Override
        public UserList createFromParcel(Parcel source) {
            return new UserList(source);
        }
        @Override
        public UserList[] newArray(int size) {
            return new UserList[size];
        }
    };

    public UserList() {}

    protected UserList(Parcel in) {
        super(in);

        users = in.createTypedArrayList(SimpleUser.CREATOR);
    }

    @Override
    public int describeContents() {
        return super.describeContents();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeTypedList(users);
    }
}
