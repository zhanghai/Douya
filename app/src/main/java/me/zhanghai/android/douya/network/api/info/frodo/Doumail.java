/*
 * Copyright (c) 2018 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * {@code Message} in Frodo.
 */
public class Doumail implements Parcelable {

    public SimpleUser author;

    public DoumailCard card;

    @SerializedName("conversation_id")
    public long conversationId;

    @SerializedName("conversation_type")
    public String conversationType;

    @SerializedName("create_time")
    public String createTime;

    public int id;

    @SerializedName("is_suspicious")
    public boolean isSuspicious;

    public long nonce;

    @SerializedName("sized_image")
    public SizedImage image;

    @SerializedName("target_uri")
    public String targetUri;

    public String text;

    public int type;


    public static final Parcelable.Creator<Doumail> CREATOR = new Parcelable.Creator<Doumail>() {
        @Override
        public Doumail createFromParcel(Parcel source) {
            return new Doumail(source);
        }
        @Override
        public Doumail[] newArray(int size) {
            return new Doumail[size];
        }
    };

    public Doumail() {}

    protected Doumail(Parcel in) {
        author = in.readParcelable(SimpleUser.class.getClassLoader());
        card = in.readParcelable(DoumailCard.class.getClassLoader());
        conversationId = in.readLong();
        conversationType = in.readString();
        createTime = in.readString();
        id = in.readInt();
        isSuspicious = in.readByte() != 0;
        nonce = in.readLong();
        image = in.readParcelable(SizedImage.class.getClassLoader());
        targetUri = in.readString();
        text = in.readString();
        type = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(author, flags);
        dest.writeParcelable(card, flags);
        dest.writeLong(conversationId);
        dest.writeString(conversationType);
        dest.writeString(createTime);
        dest.writeInt(id);
        dest.writeByte(isSuspicious ? (byte) 1 : (byte) 0);
        dest.writeLong(nonce);
        dest.writeParcelable(image, flags);
        dest.writeString(targetUri);
        dest.writeString(text);
        dest.writeInt(type);
    }
}
