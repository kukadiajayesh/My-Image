package com.app.photobook.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Jayesh on 10/11/2017.
 */

@Entity(foreignKeys = @ForeignKey(entity = Album.class,
        parentColumns = "id", childColumns = "albumId"))
public class AlbumImage implements Parcelable {

    public String localFilePath;

    @Ignore
    public int imageHeight = 0;

    @SerializedName("id")
    @Expose
    @PrimaryKey
    public Integer id;

    @SerializedName("event_id")
    @Expose
    public Integer albumId;

    @SerializedName("thumb")
    @Expose
    public String thumb;

    @SerializedName("url")
    @Expose
    public String url;

    @SerializedName("pageid")
    @Expose
    public Integer pageId;

    @SerializedName("page")
    @Expose
    public Integer page;

    @Ignore
    @SerializedName("comments")
    @Expose
    public ArrayList<Comment> comments;

    //For Selective Gallery
    @SerializedName("selected")
    @Expose
    public boolean selected = false;

    public AlbumImage() {

    }


    protected AlbumImage(Parcel in) {
        localFilePath = in.readString();
        imageHeight = in.readInt();
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            albumId = null;
        } else {
            albumId = in.readInt();
        }
        thumb = in.readString();
        url = in.readString();
        if (in.readByte() == 0) {
            pageId = null;
        } else {
            pageId = in.readInt();
        }
        if (in.readByte() == 0) {
            page = null;
        } else {
            page = in.readInt();
        }
        comments = in.createTypedArrayList(Comment.CREATOR);
        selected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(localFilePath);
        dest.writeInt(imageHeight);
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        if (albumId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(albumId);
        }
        dest.writeString(thumb);
        dest.writeString(url);
        if (pageId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(pageId);
        }
        if (page == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(page);
        }
        dest.writeTypedList(comments);
        dest.writeByte((byte) (selected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AlbumImage> CREATOR = new Creator<AlbumImage>() {
        @Override
        public AlbumImage createFromParcel(Parcel in) {
            return new AlbumImage(in);
        }

        @Override
        public AlbumImage[] newArray(int size) {
            return new AlbumImage[size];
        }
    };
}
