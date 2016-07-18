/*
 * Copyright (c) 2016 Zhang Hai <Dreaming.in.Code.ZH@Gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.douya.network.api.info.apiv2;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Photo implements Parcelable {

    @SerializedName("album_id")
    public String albumId;

    @SerializedName("album_title")
    public String albumTitle;

    public String alt;

    public User author;

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
    public boolean animated;

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
    public int recsCount;

    public PhotoSizes sizes;

    @SerializedName("thumb")
    public String thumbnail;

    public static ArrayList<Image> toImageList(ArrayList<Photo> photoList) {
        ArrayList<Image> imageList = new ArrayList<>();
        for (Photo photo : photoList) {
            Image image = new Image();
            image.medium = photo.image;
            image.width = photo.sizes.image.get(0);
            image.height = photo.sizes.image.get(1);
            imageList.add(image);
        }
        return imageList;
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
        author = in.readParcelable(User.class.getClassLoader());
        comments = in.createTypedArrayList(Comment.CREATOR);
        commentCount = in.readInt();
        cover = in.readString();
        createdAt = in.readString();
        description = in.readString();
        icon = in.readString();
        id = in.readString();
        image = in.readString();
        animated = in.readByte() != 0;
        large = in.readString();
        liked = in.readByte() != 0;
        likeCount = in.readInt();
        nextPhoto = in.readString();
        position = in.readInt();
        prevPhoto = in.readString();
        privacy = in.readString();
        recsCount = in.readInt();
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
        dest.writeByte(animated ? (byte) 1 : (byte) 0);
        dest.writeString(large);
        dest.writeByte(liked ? (byte) 1 : (byte) 0);
        dest.writeInt(likeCount);
        dest.writeString(nextPhoto);
        dest.writeInt(position);
        dest.writeString(prevPhoto);
        dest.writeString(privacy);
        dest.writeInt(recsCount);
        dest.writeParcelable(sizes, flags);
        dest.writeString(thumbnail);
    }
}
