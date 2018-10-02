package com.app.photobook.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jayesh on 10/11/2017.
 */

public class AlbumRes {

    @SerializedName("error")
    @Expose
    public int error;

    @SerializedName("msg")
    @Expose
    public String message;

    @SerializedName("data")
    @Expose
    public Album album;

    public AlbumRes() {

    }
}


