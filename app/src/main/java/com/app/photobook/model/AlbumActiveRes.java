package com.app.photobook.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Jayesh on 10/11/2017.
 */

public class AlbumActiveRes {

    @SerializedName("error")
    @Expose
    public int error;

    @SerializedName("msg")
    @Expose
    public String message;

    @SerializedName("data")
    @Expose
    public ArrayList<AlbumActive> data;

    public AlbumActiveRes() {

    }
}


