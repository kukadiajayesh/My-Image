package com.app.photobook.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jayesh on 10/11/2017.
 */

public class Maintenance {

    @SerializedName("error")
    @Expose
    public int error;

    @SerializedName("msg")
    @Expose
    public String message;

    @SerializedName("data")
    @Expose
    public Data data;

    public Maintenance() {
    }

    public class Data {
        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("is_in_maintenance")
        @Expose
        public Integer isInMaintenance;
        @SerializedName("will_maitenance")
        @Expose
        public Integer willMaitenance;
        @SerializedName("message")
        @Expose
        public String message;
        @SerializedName("version")
        @Expose
        public Integer version;
        @SerializedName("date")
        @Expose
        public String date;
    }
}


