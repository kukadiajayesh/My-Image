package com.app.photobook.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.app.photobook.tools.FileUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Jayesh on 10/11/2017.
 */

@Entity
public class Album implements Parcelable {

    public String localPath;

    @NonNull
    @PrimaryKey
    @SerializedName("event_id")
    @Expose
    public Integer id;
    @SerializedName("event_name")
    @Expose
    public String eventName;
    @SerializedName("event_path")
    @Expose
    public String eventPath;
    @SerializedName("event_type")
    @Expose
    public String eventType;
    @SerializedName("event_page_type")
    @Expose
    public Integer eventPageType;
    @SerializedName("event_start_date")
    @Expose
    public String eventStartDate;
    @SerializedName("event_end_date")
    @Expose
    public String eventEndDate;
    @SerializedName("event_mail")
    @Expose
    public String eventMail;
    @SerializedName("event_is_share")
    @Expose
    public Integer isSharble;
    @SerializedName("event_is_offline")
    @Expose
    public Integer isOffline = 1;
    @SerializedName("event_is_active")
    @Expose
    public Integer isActive;
    @SerializedName("share_message")
    @Expose
    public String shareMessage;
    @SerializedName("event_maximum_select")
    @Expose
    public Integer eventMaximumSelect;
    @SerializedName("event_size")
    @Expose
    public Integer eventSize;
    @SerializedName("event_password")
    @Expose
    public String eventPassword;
    @SerializedName("event_mail_date")
    @Expose
    public String eventMailDate;

    @SerializedName("pb_width")
    @Expose
    public String pb_width;
    @SerializedName("pb_height")
    @Expose
    public String pb_height;
    @SerializedName("totalImg")
    @Expose
    public Integer totalImg;

    @SerializedName("path")
    @Expose
    public String path;

    public long entryTime = new Date().getTime();

    @Ignore
    @SerializedName("images")
    @Expose
    public ArrayList<AlbumImage> images;

    public Album() {

    }

    protected Album(Parcel in) {
        localPath = in.readString();
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        eventName = in.readString();
        eventPath = in.readString();
        eventType = in.readString();
        if (in.readByte() == 0) {
            eventPageType = null;
        } else {
            eventPageType = in.readInt();
        }
        eventStartDate = in.readString();
        eventEndDate = in.readString();
        eventMail = in.readString();
        if (in.readByte() == 0) {
            isSharble = null;
        } else {
            isSharble = in.readInt();
        }
        if (in.readByte() == 0) {
            isOffline = null;
        } else {
            isOffline = in.readInt();
        }
        if (in.readByte() == 0) {
            isActive = null;
        } else {
            isActive = in.readInt();
        }
        shareMessage = in.readString();
        if (in.readByte() == 0) {
            eventMaximumSelect = null;
        } else {
            eventMaximumSelect = in.readInt();
        }
        if (in.readByte() == 0) {
            eventSize = null;
        } else {
            eventSize = in.readInt();
        }
        eventPassword = in.readString();
        eventMailDate = in.readString();
        pb_width = in.readString();
        pb_height = in.readString();
        if (in.readByte() == 0) {
            totalImg = null;
        } else {
            totalImg = in.readInt();
        }
        path = in.readString();
        entryTime = in.readLong();
        images = in.createTypedArrayList(AlbumImage.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(localPath);
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(eventName);
        dest.writeString(eventPath);
        dest.writeString(eventType);
        if (eventPageType == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(eventPageType);
        }
        dest.writeString(eventStartDate);
        dest.writeString(eventEndDate);
        dest.writeString(eventMail);
        if (isSharble == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(isSharble);
        }
        if (isOffline == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(isOffline);
        }
        if (isActive == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(isActive);
        }
        dest.writeString(shareMessage);
        if (eventMaximumSelect == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(eventMaximumSelect);
        }
        if (eventSize == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(eventSize);
        }
        dest.writeString(eventPassword);
        dest.writeString(eventMailDate);
        dest.writeString(pb_width);
        dest.writeString(pb_height);
        if (totalImg == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(totalImg);
        }
        dest.writeString(path);
        dest.writeLong(entryTime);
        dest.writeTypedList(images);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    private File getAlbumPathNew(Context context, boolean create) {
        String BASE_CACHE_FOLDER = FileUtils.getDefaultFolder(context);
        File BASE_IMAGE_PATH = new File(BASE_CACHE_FOLDER, FileUtils.FOLDER_IMAGES +
                File.separator + id);
        if (!BASE_IMAGE_PATH.exists() && create) {
            BASE_IMAGE_PATH.mkdirs();
        }
        return BASE_IMAGE_PATH;
    }

    private File getAlbumPathOld(Context context) {
        String BASE_CACHE_FOLDER = FileUtils.getDefaultFolder(context);
        return new File(BASE_CACHE_FOLDER + id);
    }

    public File getAlbumPath(Context context, boolean create) {
        File newPath = getAlbumPathNew(context, create);
        if (newPath.exists()) {
            return newPath;
        }
        return getAlbumPathOld(context);
    }

}


/*
{
    "error": false,
    "mesage": "",
    "id": "2",
    "pb_name": "Sara & Sultan",
    "pb_password": "e0f65f",
    "pb_type": "Premium Photobook",
    "pb_orientation": "Landscape",
    "pb_direction": "ltr",
    "photography_by": "Liza Hakimi",
    "pb_is_double": "1",
    "comment_date": "2017-10-13",
    "total_img": 21,
    "images": [
        {
            "thumb": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/thumb/front-cover.jpg",
            "main": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/front-cover.jpg",
            "page": 1
        },
        {
            "thumb": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/thumb/2.jpg",
            "main": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/2.jpg",
            "page": 2
        },
        {
            "thumb": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/thumb/3.jpg",
            "main": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/3.jpg",
            "page": 2
        },
        {
            "thumb": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/thumb/4.jpg",
            "main": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/4.jpg",
            "page": 3
        },
        {
            "thumb": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/thumb/5.jpg",
            "main": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/5.jpg",
            "page": 3
        },
        {
            "thumb": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/thumb/6.jpg",
            "main": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/6.jpg",
            "page": 4
        },
        {
            "thumb": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/thumb/7.jpg",
            "main": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/7.jpg",
            "page": 4
        },
        {
            "thumb": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/thumb/8.jpg",
            "main": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/8.jpg",
            "page": 5
        },
        {
            "thumb": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/thumb/9.jpg",
            "main": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/9.jpg",
            "page": 5
        },
        {
            "thumb": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/thumb/10.jpg",
            "main": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/10.jpg",
            "page": 6
        },
        {
            "thumb": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/thumb/11.jpg",
            "main": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/11.jpg",
            "page": 6
        },
        {
            "thumb": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/thumb/12.jpg",
            "main": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/12.jpg",
            "page": 7
        },
        {
            "thumb": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/thumb/13.jpg",
            "main": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/13.jpg",
            "page": 7
        },
        {
            "thumb": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/thumb/14.jpg",
            "main": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/14.jpg",
            "page": 8
        },
        {
            "thumb": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/thumb/15.jpg",
            "main": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/15.jpg",
            "page": 8
        },
        {
            "thumb": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/thumb/16.jpg",
            "main": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/16.jpg",
            "page": 9
        },
        {
            "thumb": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/thumb/17.jpg",
            "main": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/17.jpg",
            "page": 9
        },
        {
            "thumb": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/thumb/18.jpg",
            "main": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/18.jpg",
            "page": 10
        },
        {
            "thumb": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/thumb/19.jpg",
            "main": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/19.jpg",
            "page": 10
        },
        {
            "thumb": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/thumb/20.jpg",
            "main": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/20.jpg",
            "page": 11
        },
        {
            "thumb": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/thumb/21.jpg",
            "main": "http://albums.gulfphotobook.com/wp-content/uploads/aLiveFoto_Book/sara-sultan/21.jpg",
            "page": 11
        }
    ]
}
 */
