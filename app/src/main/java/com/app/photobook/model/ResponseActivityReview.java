package com.app.photobook.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jayesh on 10/11/2017.
 */

public class ResponseActivityReview implements Parcelable {

    public int activity_id, status,reviewed_status;
    public String requesting_username, activity_title;

    public ResponseActivityReview() {

    }

    protected ResponseActivityReview(Parcel in) {
        activity_id = in.readInt();
        status = in.readInt();
        reviewed_status = in.readInt();
        requesting_username = in.readString();
        activity_title = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(activity_id);
        dest.writeInt(status);
        dest.writeInt(reviewed_status);
        dest.writeString(requesting_username);
        dest.writeString(activity_title);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ResponseActivityReview> CREATOR = new Creator<ResponseActivityReview>() {
        @Override
        public ResponseActivityReview createFromParcel(Parcel in) {
            return new ResponseActivityReview(in);
        }

        @Override
        public ResponseActivityReview[] newArray(int size) {
            return new ResponseActivityReview[size];
        }
    };

    @Override
    public String toString() {
        return "ResponseActivityStatus{" +
                "reward_id=" + activity_id +
                ", status=" + reviewed_status +
                ", user_name='" + requesting_username + '\'' +
                ", activity_title='" + activity_title + '\'' +
                '}';
    }
}
