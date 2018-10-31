package com.app.photobook.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jayesh on 10/11/2017.
 */

public class AlbumActive implements Parcelable {

    @NonNull
    @SerializedName("event_id")
    @Expose
    public Integer id;

    @SerializedName("event_is_active")
    @Expose
    public Integer isActive;

    public AlbumActive() {

    }

    protected AlbumActive(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            isActive = null;
        } else {
            isActive = in.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        if (isActive == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(isActive);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AlbumActive> CREATOR = new Creator<AlbumActive>() {
        @Override
        public AlbumActive createFromParcel(Parcel in) {
            return new AlbumActive(in);
        }

        @Override
        public AlbumActive[] newArray(int size) {
            return new AlbumActive[size];
        }
    };
}

/*
{
    "error": 0,
    "msg": "success",
    "data": [
        {
            "event_id": 6,
            "event_is_active": 1
        },
        {
            "event_id": 33,
            "event_is_active": 1
        }
    ]
}
 */
