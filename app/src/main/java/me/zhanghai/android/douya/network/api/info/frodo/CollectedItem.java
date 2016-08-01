/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import me.zhanghai.android.douya.R;

public class CollectedItem implements Parcelable {

    public enum State {

        TODO("wish", R.string.item_todo_format),
        DOING("do", R.string.item_doing_format),
        DONE("collect", R.string.item_done_format);

        private String apiString;
        private int formatRes;

        State(String apiString, int formatRes) {
            this.apiString = apiString;
            this.formatRes = formatRes;
        }

        public static State ofString(String apiString, State defaultValue) {
            for (State state : State.values()) {
                if (TextUtils.equals(state.apiString, apiString)) {
                    return state;
                }
            }
            return defaultValue;
        }

        public static State ofString(String apiString) {
            return ofString(apiString, DONE);
        }

        /**
         * @deprecated HACK-only.
         */
        public String getApiString() {
            return apiString;
        }

        public int getFormatRes() {
            return formatRes;
        }

        public String getFormat(Context context) {
            return context.getString(formatRes);
        }

        public String getString(String action, Context context) {
            return context.getString(formatRes, action);
        }

        public String getString(Item.Type type, Context context) {
            return getString(type.getAction(context), context);
        }
    }

    @SerializedName("attend_time")
    public String attendTime;

    public String comment;

    @SerializedName("create_time")
    public String createdAt;

    @SerializedName("done_index")
    public int doneIndex;

    public long id;

    @SerializedName("index")
    public int collectedIndex;

    public ArrayList<GamePlatform> platforms = new ArrayList<>();

    @SerializedName("popular_tags")
    public ArrayList<String> popularTags = new ArrayList<>();

    public Rating rating;

    @SerializedName("sharing_url")
    public String shareUrl;

    /**
     * @deprecated Use {@link #getState()} instead.
     */
    @SerializedName("status")
    public String state;

    @SerializedName("is_voted")
    public boolean isVoted;

    @SerializedName("subject")
    public Item item;

    public ArrayList<String> tags = new ArrayList<>();

    @SerializedName("vote_count")
    public int voteCount;

    public State getState() {
        //noinspection deprecation
        return State.ofString(state);
    }


    public static final Parcelable.Creator<CollectedItem> CREATOR =
            new Parcelable.Creator<CollectedItem>() {
                @Override
                public CollectedItem createFromParcel(Parcel source) {
                    return new CollectedItem(source);
                }
                @Override
                public CollectedItem[] newArray(int size) {
                    return new CollectedItem[size];
                }
            };

    public CollectedItem() {}

    protected CollectedItem(Parcel in) {
        attendTime = in.readString();
        comment = in.readString();
        createdAt = in.readString();
        doneIndex = in.readInt();
        id = in.readLong();
        collectedIndex = in.readInt();
        platforms = in.createTypedArrayList(GamePlatform.CREATOR);
        popularTags = in.createStringArrayList();
        rating = in.readParcelable(Rating.class.getClassLoader());
        shareUrl = in.readString();
        //noinspection deprecation
        state = in.readString();
        isVoted = in.readByte() != 0;
        item = in.readParcelable(Item.class.getClassLoader());
        tags = in.createStringArrayList();
        voteCount = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(attendTime);
        dest.writeString(comment);
        dest.writeString(createdAt);
        dest.writeInt(doneIndex);
        dest.writeLong(id);
        dest.writeInt(collectedIndex);
        dest.writeTypedList(platforms);
        dest.writeStringList(popularTags);
        dest.writeParcelable(rating, flags);
        dest.writeString(shareUrl);
        //noinspection deprecation
        dest.writeString(state);
        dest.writeByte(isVoted ? (byte) 1 : (byte) 0);
        dest.writeParcelable(item, flags);
        dest.writeStringList(tags);
        dest.writeInt(voteCount);
    }
}
