/*
 * Copyright (c) 2017 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.frodo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TimelineItem extends BaseItem {

    public enum Type {

        AD_CONTENT(15),
        AD_FOUR_IMAGES(21),
        AD_ONE_LARGE_IMAGE(6),
        AD_VIDEO(5),
        ALBUM(3),
        ALBUM_DEFAULT(11),
        BANNER(17),
        DEFAULT_CONTENT_RECTANGLE(2),
        DEFAULT_CONTENT_SQUARE(2),
        FOLD_PHOTO(4),
        NAV_TAB(13),
        ONE_LARGE_IMAGE(9),
        RECOMMEND_SUBJECT_LAYOUT(9),
        RECOMMEND_TOPICS(7),
        STATUS(1),
        TOPIC_CARD(16),
        VIDEO_DEFAULT(8);

        private int mApiInt;

        Type(int apiInt) {
            mApiInt = apiInt;
        }

        public static Type ofApiInt(int apiInt, Type defaultValue) {
            for (Type type : Type.values()) {
                if (type.mApiInt == apiInt) {
                    return type;
                }
            }
            return defaultValue;
        }

        public static Type ofApiInt(int apiInt) {
            return ofApiInt(apiInt, null);
        }

        public int getApiInt() {
            return mApiInt;
        }
    }

    public String action;

    //@SerializedName("ad_info")
    //public FeedAD adInfo;

    public ArrayList<Comment> comments = new ArrayList<>();

    @SerializedName("comments_count")
    public int commentCount;

    public Content content;

    @SerializedName("created_time")
    public String createTime;

    @SerializedName("creationCount")
    public int createCount;

    public long enterTime;

    public boolean exposed;

    @SerializedName("fold_key")
    public String foldKey;

    @SerializedName("is_collected")
    public boolean isCollected;

    public boolean isRead;

    /**
     * @deprecated Use {@link #getType()} instead.
     */
    @SerializedName("layout")
    public int type;

    public Type getType() {
        //noinspection deprecation
        return Type.ofApiInt(type);
    }

    @SerializedName("more_item_count")
    public int moreItemCount;

    public NotificationList notifications;

    public Owner owner;

    @SerializedName("owner_alter_label")
    public OwnerAlternativeLabel ownerAlternaitveLabel;

    @SerializedName("reaction_type")
    public int reactionType;

    @SerializedName("reactions_count")
    public int reactionCount;

    // com.douban.frodo.model.common.RecInfo
    //@SerializedName("rec_info")
    //public RecommendationInfo recommendationInfo;

    @SerializedName("resharer")
    public SimpleUser rebroadcaster;

    @SerializedName("reshares_count")
    public int rebroadcastCount;

    @SerializedName("show_actions")
    public boolean showActions;

    //public int skynetEntryStatus;

    // TODO
    // com.douban.frodo.model.common.StatusGalleryTopic
    //public BroadcastTopic topic;

    @SerializedName("uid")
    public String id;


    public static final Creator<TimelineItem> CREATOR = new Creator<TimelineItem>() {
        @Override
        public TimelineItem createFromParcel(Parcel source) {
            return new TimelineItem(source);
        }
        @Override
        public TimelineItem[] newArray(int size) {
            return new TimelineItem[size];
        }
    };

    public TimelineItem() {}

    protected TimelineItem(Parcel in) {
        super(in);

        action = in.readString();
        comments = in.createTypedArrayList(Comment.CREATOR);
        commentCount = in.readInt();
        content = in.readParcelable(Content.class.getClassLoader());
        createTime = in.readString();
        createCount = in.readInt();
        enterTime = in.readLong();
        exposed = in.readByte() != 0;
        foldKey = in.readString();
        isCollected = in.readByte() != 0;
        isRead = in.readByte() != 0;
        //noinspection deprecation
        type = in.readInt();
        moreItemCount = in.readInt();
        notifications = in.readParcelable(NotificationList.class.getClassLoader());
        owner = in.readParcelable(Owner.class.getClassLoader());
        ownerAlternaitveLabel = in.readParcelable(OwnerAlternativeLabel.class.getClassLoader());
        reactionType = in.readInt();
        reactionCount = in.readInt();
        rebroadcaster = in.readParcelable(SimpleUser.class.getClassLoader());
        rebroadcastCount = in.readInt();
        showActions = in.readByte() != 0;
        id = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(action);
        dest.writeTypedList(comments);
        dest.writeInt(commentCount);
        dest.writeParcelable(content, flags);
        dest.writeString(createTime);
        dest.writeInt(createCount);
        dest.writeLong(enterTime);
        dest.writeByte(exposed ? (byte) 1 : (byte) 0);
        dest.writeString(foldKey);
        dest.writeByte(isCollected ? (byte) 1 : (byte) 0);
        dest.writeByte(isRead ? (byte) 1 : (byte) 0);
        //noinspection deprecation
        dest.writeInt(type);
        dest.writeInt(moreItemCount);
        dest.writeParcelable(notifications, flags);
        dest.writeParcelable(owner, flags);
        dest.writeParcelable(ownerAlternaitveLabel, flags);
        dest.writeInt(reactionType);
        dest.writeInt(reactionCount);
        dest.writeParcelable(rebroadcaster, flags);
        dest.writeInt(rebroadcastCount);
        dest.writeByte(showActions ? (byte) 1 : (byte) 0);
        dest.writeString(id);
    }


    public static class Content extends BaseItem {

        @SerializedName("alter_author_string")
        public String alternativeAuthor;

        public SimpleUser author;

        @SerializedName("card")
        public BroadcastAttachment attachment;

        public Photo photo;

        public ArrayList<Photo> photos = new ArrayList<>();

        @SerializedName("photos_count")
        public int photoCount;

        @SerializedName("status")
        public Broadcast broadcast;

        public String text;

        //com.douban.frodo.model.feed.ad.VideoInfo;
        //@SerializedName("video_info")
        //public VideoInfo videoInfo;


        public static final Creator<Content> CREATOR = new Creator<Content>() {
            @Override
            public Content createFromParcel(Parcel source) {
                return new Content(source);
            }
            @Override
            public Content[] newArray(int size) {
                return new Content[size];
            }
        };

        public Content() {}

        protected Content(Parcel in) {
            super(in);

            alternativeAuthor = in.readString();
            author = in.readParcelable(SimpleUser.class.getClassLoader());
            attachment = in.readParcelable(BroadcastAttachment.class.getClassLoader());
            photo = in.readParcelable(Photo.class.getClassLoader());
            photos = in.createTypedArrayList(Photo.CREATOR);
            photoCount = in.readInt();
            broadcast = in.readParcelable(Broadcast.class.getClassLoader());
            text = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);

            dest.writeString(alternativeAuthor);
            dest.writeParcelable(author, flags);
            dest.writeParcelable(attachment, flags);
            dest.writeParcelable(photo, flags);
            dest.writeTypedList(photos);
            dest.writeInt(photoCount);
            dest.writeParcelable(broadcast, flags);
            dest.writeString(text);
        }
    }

    public static class Owner implements Parcelable {

        public String avatar;

        @SerializedName("event_label")
        public String eventLabel;

        public String id;

        @SerializedName("is_rect_avatar")
        public boolean isAvatarRectangular;

        public String name;

        public String type;

        public String uri;

        public String url;

        @SerializedName("verify_type")
        public int verifyType;


        public static final Creator<Owner> CREATOR = new Creator<Owner>() {
            @Override
            public Owner createFromParcel(Parcel source) {
                return new Owner(source);
            }
            @Override
            public Owner[] newArray(int size) {
                return new Owner[size];
            }
        };

        public Owner() {}

        protected Owner(Parcel in) {
            avatar = in.readString();
            eventLabel = in.readString();
            id = in.readString();
            isAvatarRectangular = in.readByte() != 0;
            name = in.readString();
            type = in.readString();
            uri = in.readString();
            url = in.readString();
            verifyType = in.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(avatar);
            dest.writeString(eventLabel);
            dest.writeString(id);
            dest.writeByte(isAvatarRectangular ? (byte) 1 : (byte) 0);
            dest.writeString(name);
            dest.writeString(type);
            dest.writeString(uri);
            dest.writeString(url);
            dest.writeInt(verifyType);
        }
    }

    public static class OwnerAlternativeLabel implements Parcelable {

        public String text;


        public static final Creator<OwnerAlternativeLabel> CREATOR =
                new Creator<OwnerAlternativeLabel>() {
                    @Override
                    public OwnerAlternativeLabel createFromParcel(Parcel source) {
                        return new OwnerAlternativeLabel(source);
                    }
                    @Override
                    public OwnerAlternativeLabel[] newArray(int size) {
                        return new OwnerAlternativeLabel[size];
                    }
                };

        public OwnerAlternativeLabel() {}

        protected OwnerAlternativeLabel(Parcel in) {
            text = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(text);
        }
    }
}
