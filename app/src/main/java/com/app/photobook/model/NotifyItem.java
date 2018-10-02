package com.app.photobook.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jayesh on 10/11/2017.
 */

public class NotifyItem implements Parcelable {

    public int id, status;
    public String json, entry_time;

    public NotifyItem() {

    }

    protected NotifyItem(Parcel in) {
        id = in.readInt();
        json = in.readString();
        entry_time = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(json);
        dest.writeString(entry_time);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NotifyItem> CREATOR = new Creator<NotifyItem>() {
        @Override
        public NotifyItem createFromParcel(Parcel in) {
            return new NotifyItem(in);
        }

        @Override
        public NotifyItem[] newArray(int size) {
            return new NotifyItem[size];
        }
    };
}
