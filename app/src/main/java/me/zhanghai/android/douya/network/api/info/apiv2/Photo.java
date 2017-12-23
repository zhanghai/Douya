/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.apiv2;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import me.zhanghai.android.douya.network.api.info.frodo.Comment;
import me.zhanghai.android.douya.ui.SizedImageItem;

public class Photo implements SizedImageItem, Parcelable {

    @SerializedName("album_id")
    public String albumId;

    @SerializedName("album_title")
    public String albumTitle;

    public String alt;

    public SimpleUser author;

    public ArrayList<Comment> comments = new ArrayList<>();

    @SerializedName("comments_count")
    public int commentCount;

    public String cover;

    @SerializedName("created")
    public String createdAt;

    @SerializedName("desc")
    public String description;

    public String icon;

    public String id;

    public String image;

    @SerializedName("is_animated")
    public boolean isAnimated;

    public String large;

    public boolean liked;

    @SerializedName("liked_count")
    public int likeCount;

    @SerializedName("next_photo")
    public String nextPhoto;

    public int position;

    @SerializedName("prev_photo")
    public String prevPhoto;

    public String privacy;

    @SerializedName("recs_count")
    public int recommendCount;

    public PhotoSizes sizes;

    @SerializedName("thumb")
    public String thumbnail;


    @Override
    public String getLargeUrl() {
        return large != null ? large
                : image != null ? image
                : cover != null ? cover
                : thumbnail != null ? thumbnail
                : icon;
    }

    @Override
    public int getLargeWidth() {
        return large != null ? sizes.large.get(0)
                : image != null ? sizes.image.get(0)
                : cover != null ? sizes.cover.get(0)
                : thumbnail != null ? sizes.thumbnail.get(0)
                : sizes.icon.get(0);
    }

    @Override
    public int getLargeHeight() {
        return large != null ? sizes.large.get(1)
                : image != null ? sizes.image.get(1)
                : cover != null ? sizes.cover.get(1)
                : thumbnail != null ? sizes.thumbnail.get(1)
                : sizes.icon.get(1);
    }

    @Override
    public String getMediumUrl() {
        return image != null ? image
                : large != null ? large
                : cover != null ? cover
                : thumbnail != null ? thumbnail
                : icon;
    }

    @Override
    public int getMediumWidth() {
        return image != null ? sizes.image.get(0)
                : large != null ? sizes.large.get(0)
                : cover != null ? sizes.cover.get(0)
                : thumbnail != null ? sizes.thumbnail.get(0)
                : sizes.icon.get(0);
    }

    @Override
    public int getMediumHeight() {
        return image != null ? sizes.image.get(1)
                : large != null ? sizes.large.get(1)
                : cover != null ? sizes.cover.get(1)
                : thumbnail != null ? sizes.thumbnail.get(1)
                : sizes.icon.get(1);
    }

    @Override
    public String getSmallUrl() {
        return cover != null ? cover
                : thumbnail != null ? thumbnail
                : icon != null ? icon
                : image != null ? image
                : large;
    }

    @Override
    public int getSmallWidth() {
        return cover != null ? sizes.cover.get(0)
                : thumbnail != null ? sizes.thumbnail.get(0)
                : icon != null ? sizes.icon.get(0)
                : image != null ? sizes.image.get(0)
                : sizes.large.get(0);
    }

    @Override
    public int getSmallHeight() {
        return cover != null ? sizes.cover.get(1)
                : thumbnail != null ? sizes.thumbnail.get(1)
                : icon != null ? sizes.icon.get(1)
                : image != null ? sizes.image.get(1)
                : sizes.large.get(1);
    }

    @Override
    public boolean isAnimated() {
        return isAnimated;
    }


    public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
        public Photo createFromParcel(Parcel source) {
            return new Photo(source);
        }
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public Photo() {}

    protected Photo(Parcel in) {
        albumId = in.readString();
        albumTitle = in.readString();
        alt = in.readString();
        author = in.readParcelable(SimpleUser.class.getClassLoader());
        comments = in.createTypedArrayList(Comment.CREATOR);
        commentCount = in.readInt();
        cover = in.readString();
        createdAt = in.readString();
        description = in.readString();
        icon = in.readString();
        id = in.readString();
        image = in.readString();
        isAnimated = in.readByte() != 0;
        large = in.readString();
        liked = in.readByte() != 0;
        likeCount = in.readInt();
        nextPhoto = in.readString();
        position = in.readInt();
        prevPhoto = in.readString();
        privacy = in.readString();
        recommendCount = in.readInt();
        sizes = in.readParcelable(PhotoSizes.class.getClassLoader());
        thumbnail = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(albumId);
        dest.writeString(albumTitle);
        dest.writeString(alt);
        dest.writeParcelable(author, 0);
        dest.writeTypedList(comments);
        dest.writeInt(commentCount);
        dest.writeString(cover);
        dest.writeString(createdAt);
        dest.writeString(description);
        dest.writeString(icon);
        dest.writeString(id);
        dest.writeString(image);
        dest.writeByte(isAnimated ? (byte) 1 : (byte) 0);
        dest.writeString(large);
        dest.writeByte(liked ? (byte) 1 : (byte) 0);
        dest.writeInt(likeCount);
        dest.writeString(nextPhoto);
        dest.writeInt(position);
        dest.writeString(prevPhoto);
        dest.writeString(privacy);
        dest.writeInt(recommendCount);
        dest.writeParcelable(sizes, flags);
        dest.writeString(thumbnail);
    }
}
