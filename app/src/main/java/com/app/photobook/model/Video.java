package com.app.photobook.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jayesh on 10/11/2017.
 */
public class Video implements Parcelable {

    @SerializedName("video_id")
    @Expose
    public int videoId;
    @SerializedName("video_title")
    @Expose
    public String videoTitle;
    @SerializedName("video_url")
    @Expose
    public String videoUrl;
    @SerializedName("video_desc")
    @Expose
    public String videoDesc;
    @SerializedName("video_user_id")
    @Expose
    public int videoUserId;
    @SerializedName("video_type")
    @Expose
    public int videoType;
    @SerializedName("iframe_url")
    @Expose
    public String iframeUrl;
    @SerializedName("query_string")
    @Expose
    public String queryString;
    public String youtubeId;


    public Video() {}

    protected Video(Parcel in) {
        videoId = in.readInt();
        videoTitle = in.readString();
        videoUrl = in.readString();
        videoDesc = in.readString();
        videoUserId = in.readInt();
        videoType = in.readInt();
        iframeUrl = in.readString();
        queryString = in.readString();
        youtubeId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(videoId);
        dest.writeString(videoTitle);
        dest.writeString(videoUrl);
        dest.writeString(videoDesc);
        dest.writeInt(videoUserId);
        dest.writeInt(videoType);
        dest.writeString(iframeUrl);
        dest.writeString(queryString);
        dest.writeString(youtubeId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}
