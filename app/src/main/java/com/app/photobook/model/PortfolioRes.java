package com.app.photobook.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Jayesh on 10/11/2017.
 */

public class PortfolioRes {

    @SerializedName("error")
    @Expose
    public int error;

    @SerializedName("msg")
    @Expose
    public String message;

    @SerializedName("data")
    @Expose
    public Data data;

    public PortfolioRes() {

    }

    public class Data {
        @SerializedName("album")
        @Expose
        public ArrayList<Album> albums = null;
        @SerializedName("video")
        @Expose
        public ArrayList<Video> videos = null;
    }
}


