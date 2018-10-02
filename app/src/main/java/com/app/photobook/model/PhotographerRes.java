package com.app.photobook.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jayesh on 10/11/2017.
 */

public class PhotographerRes {

    @SerializedName("error")
    @Expose
    public int error;

    @SerializedName("data")
    @Expose
    public Photographer data;

    public PhotographerRes() {}
}

/*

{
    "error": false,
    "cilent_detail": {
        "id": "2",
        "cl_name": "aLiveFoto",
        "cl_email": "pareshsrathod@gmail.com",
        "cl_mobile": "937123484",
        "cl_address": "",
        "cl_abouts": "",
        "cl_logo": "a5968bc7569b32.png",
        "logo_url": "http://design.alivefoto.com/wp-content/uploads/aLiveFoto_Book/clients/a5968bc7569b32.png"
    }
}
 */
