package com.app.photobook.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jayesh on 10/11/2017.
 */

public class Comment implements Parcelable {

    public int event_id, image_id, user_id;
    public String user_name, comment, date;

    protected Comment(Parcel in) {
        event_id = in.readInt();
        image_id = in.readInt();
        user_id = in.readInt();
        user_name = in.readString();
        comment = in.readString();
        date = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(event_id);
        dest.writeInt(image_id);
        dest.writeInt(user_id);
        dest.writeString(user_name);
        dest.writeString(comment);
        dest.writeString(date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}
