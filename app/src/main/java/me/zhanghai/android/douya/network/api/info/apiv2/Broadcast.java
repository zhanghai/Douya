/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.apiv2;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import me.zhanghai.android.douya.R;
import me.zhanghai.android.douya.util.TimeUtils;

public class Broadcast implements Parcelable {

    @SerializedName("activity")
    public String action;

    public Attachment attachment;

    public User author;

    @SerializedName("can_reply")
    public int canCommentInt;

    @SerializedName("comments_count")
    public int commentCount;

    @SerializedName("created_at")
    public String createdAt;

    public ArrayList<Entity> entities = new ArrayList<>();

    public long id;

    @SerializedName("interest_type")
    public String interestType;

    @SerializedName("is_interest")
    public boolean isInterest;

    @SerializedName("liked")
    public boolean isLiked;

    @SerializedName("like_count")
    public int likeCount;

    @SerializedName("media")
    public ArrayList<Image> images = new ArrayList<>();

    public ArrayList<Photo> photos = new ArrayList<>();

    @SerializedName("reshare_id")
    public Long rebroadcastId;

    @SerializedName("reshared_count")
    public int rebroadcastCount;

    @SerializedName("reshared_status")
    public Broadcast rebroadcastedBroadcast;

    private transient boolean rebroadcastedFix = false;

    public String source;

    @SerializedName("text")
    public String text;

    public String title;

    public String type;

    public boolean isAuthorOneself(Context context) {
        return author != null && author.isOneself(context);
    }

    public String getAuthorName() {
        return isInterest ? interestType : author.name;
    }

    public CharSequence getTextWithEntities(Context context) {
        return Entity.applyEntities(text, entities, context);
    }

    public void fixLiked(boolean liked) {
        if (isLiked != liked) {
            isLiked = liked;
            if (isLiked) {
                ++likeCount;
            } else {
                --likeCount;
            }
        }
    }

    public boolean canComment() {
        return canCommentInt != 0;
    }

    public boolean isRebroadcasted() {
        return rebroadcastId != null || rebroadcastedFix;
    }

    public void fixRebroadcasted(boolean rebroadcasted) {
        if (isRebroadcasted() != rebroadcasted) {
            if (rebroadcasted) {
                rebroadcastedFix = true;
                ++rebroadcastCount;
            } else {
                rebroadcastId = null;
                rebroadcastedFix = false;
                --rebroadcastCount;
            }
        }
    }

    public String getRebroadcastedBy(Context context) {
        return rebroadcastedBroadcast != null ?
                context.getString(R.string.broadcast_rebroadcasted_by_format, getAuthorName())
                : null;
    }

    /**
     * Use {@link me.zhanghai.android.douya.ui.TimeActionTextView} instead if the text is to be set
     * on a {@code TextView}.
     */
    public String getActionWithTime(Context context) {
        return context.getString(R.string.broadcast_time_action_format,
                TimeUtils.formatDoubanDateTime(createdAt, context), action);
    }

    public String getLikeCountString() {
        return likeCount == 0 ? null : String.valueOf(likeCount);
    }

    public String getCommentCountString() {
        return commentCount == 0 ? null : String.valueOf(commentCount);
    }

    public String getClipboradLabel() {
        return getAuthorName();
    }

    public String getClipboardText(Context context) {
        StringBuilder builder = new StringBuilder()
                .append(getAuthorName())
                .append(' ')
                .append(getActionWithTime(context));
        if (attachment != null) {
            builder.append('\n')
                    .append(attachment.title)
                    .append('\n')
                    .append(attachment.href)
                    .append('\n')
                    .append(attachment.description);
        }
        if (!TextUtils.isEmpty(text)) {
            builder
                    .append('\n')
                    .append(getTextWithEntities(context));
        }
        return builder.toString();
    }

    public static String makeTransitionName(long id) {
        return "broadcast-" + id;
    }

    public String makeTransitionName() {
        return makeTransitionName(id);
    }

    public static final Parcelable.Creator<Broadcast> CREATOR =
            new Parcelable.Creator<Broadcast>() {
                public Broadcast createFromParcel(Parcel source) {
                    return new Broadcast(source);
                }
                public Broadcast[] newArray(int size) {
                    return new Broadcast[size];
                }
            };

    public Broadcast() {}

    protected Broadcast(Parcel in) {
        action = in.readString();
        attachment = in.readParcelable(Attachment.class.getClassLoader());
        author = in.readParcelable(User.class.getClassLoader());
        canCommentInt = in.readInt();
        commentCount = in.readInt();
        createdAt = in.readString();
        entities = in.createTypedArrayList(Entity.CREATOR);
        id = in.readLong();
        interestType = in.readString();
        isInterest = in.readByte() != 0;
        isLiked = in.readByte() != 0;
        likeCount = in.readInt();
        images = in.createTypedArrayList(Image.CREATOR);
        photos = in.createTypedArrayList(Photo.CREATOR);
        rebroadcastId = (Long) in.readValue(Long.class.getClassLoader());
        rebroadcastCount = in.readInt();
        rebroadcastedBroadcast = in.readParcelable(Broadcast.class.getClassLoader());
        rebroadcastedFix = in.readByte() != 0;
        source = in.readString();
        text = in.readString();
        title = in.readString();
        type = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(action);
        dest.writeParcelable(attachment, 0);
        dest.writeParcelable(author, 0);
        dest.writeInt(canCommentInt);
        dest.writeInt(commentCount);
        dest.writeString(createdAt);
        dest.writeTypedList(entities);
        dest.writeLong(id);
        dest.writeString(interestType);
        dest.writeByte(isInterest ? (byte) 1 : (byte) 0);
        dest.writeByte(isLiked ? (byte) 1 : (byte) 0);
        dest.writeInt(likeCount);
        dest.writeTypedList(images);
        dest.writeTypedList(photos);
        dest.writeValue(rebroadcastId);
        dest.writeInt(rebroadcastCount);
        dest.writeParcelable(rebroadcastedBroadcast, flags);
        dest.writeByte(rebroadcastedFix ? (byte) 1 : (byte) 0);
        dest.writeString(source);
        dest.writeString(text);
        dest.writeString(title);
        dest.writeString(type);
    }
}
