package com.app.photobook.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jayesh on 10/11/2017.
 */

public class ResponseActivityStatus implements Parcelable {

    public int activity_id, status;
    public String user_name, activity_title;

    public ResponseActivityStatus() {

    }

    protected ResponseActivityStatus(Parcel in) {
        activity_id = in.readInt();
        status = in.readInt();
        user_name = in.readString();
        activity_title = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(activity_id);
        dest.writeInt(status);
        dest.writeString(user_name);
        dest.writeString(activity_title);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ResponseActivityStatus> CREATOR = new Creator<ResponseActivityStatus>() {
        @Override
        public ResponseActivityStatus createFromParcel(Parcel in) {
            return new ResponseActivityStatus(in);
        }

        @Override
        public ResponseActivityStatus[] newArray(int size) {
            return new ResponseActivityStatus[size];
        }
    };

    @Override
    public String toString() {
        return "ResponseActivityStatus{" +
                "activity_id=" + activity_id +
                ", status=" + status +
                ", user_name='" + user_name + '\'' +
                ", activity_title='" + activity_title + '\'' +
                '}';
    }
}
