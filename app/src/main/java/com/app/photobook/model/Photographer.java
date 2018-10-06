package com.app.photobook.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jayesh on 10/11/2017.
 */

@Entity
public class Photographer implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public int rowId;

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("business_name")
    @Expose
    public String businessName;
    @SerializedName("website")
    @Expose
    public String website;
    @SerializedName("first_name")
    @Expose
    public String firstName;
    @SerializedName("mobile")
    @Expose
    public String mobile;
    @SerializedName("address")
    @Expose
    public String address;
    @SerializedName("pincode")
    @Expose
    public String pincode;
    @SerializedName("city")
    @Expose
    public String city;
    @SerializedName("state")
    @Expose
    public String state;
    @SerializedName("country")
    @Expose
    public String country;
    @SerializedName("profile_picture")
    @Expose
    public String profilePicture;
    @SerializedName("logo")
    @Expose
    public String logo;
    @SerializedName("biography")
    @Expose
    public String biography;
    @SerializedName("facebook_link")
    @Expose
    public String facebookLink;
    @SerializedName("twitter_link")
    @Expose
    public String twitterLink;
    @SerializedName("googleplus_link")
    @Expose
    public String googleplusLink;
    @SerializedName("instagram_link")
    @Expose
    public String instagramLink;
    @SerializedName("linkedin_link")
    @Expose
    public String linkedinLink;
    @SerializedName("pinterest_link")
    @Expose
    public String pinterestLink;
    @SerializedName("portfolio_label")
    @Expose
    public String portfolioLabel;
    @SerializedName("private_gallery_label")
    @Expose
    public String privateGalleryLabel;
    @SerializedName("google_map_direction")
    @Expose
    public String googleMapDirection;


    public Photographer() {

    }

    protected Photographer(Parcel in) {
        rowId = in.readInt();
        id = in.readString();
        email = in.readString();
        businessName = in.readString();
        website = in.readString();
        firstName = in.readString();
        mobile = in.readString();
        address = in.readString();
        pincode = in.readString();
        city = in.readString();
        state = in.readString();
        country = in.readString();
        profilePicture = in.readString();
        logo = in.readString();
        biography = in.readString();
        facebookLink = in.readString();
        twitterLink = in.readString();
        googleplusLink = in.readString();
        instagramLink = in.readString();
        linkedinLink = in.readString();
        pinterestLink = in.readString();
        portfolioLabel = in.readString();
        privateGalleryLabel = in.readString();
        googleMapDirection = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(rowId);
        dest.writeString(id);
        dest.writeString(email);
        dest.writeString(businessName);
        dest.writeString(website);
        dest.writeString(firstName);
        dest.writeString(mobile);
        dest.writeString(address);
        dest.writeString(pincode);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(country);
        dest.writeString(profilePicture);
        dest.writeString(logo);
        dest.writeString(biography);
        dest.writeString(facebookLink);
        dest.writeString(twitterLink);
        dest.writeString(googleplusLink);
        dest.writeString(instagramLink);
        dest.writeString(linkedinLink);
        dest.writeString(pinterestLink);
        dest.writeString(portfolioLabel);
        dest.writeString(privateGalleryLabel);
        dest.writeString(googleMapDirection);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Photographer> CREATOR = new Creator<Photographer>() {
        @Override
        public Photographer createFromParcel(Parcel in) {
            return new Photographer(in);
        }

        @Override
        public Photographer[] newArray(int size) {
            return new Photographer[size];
        }
    };
}

/*

"id": "2",
"cl_name": "aLiveFoto",
"cl_email": "pareshsrathod@gmail.com",
"cl_mobile": "937123484",
"cl_address": "",
"cl_abouts": "",
"cl_logo": "a5968bc7569b32.png",
"logo_url": "http://design.alivefoto.com/wp-content/uploads/aLiveFoto_Book/clients/a5968bc7569b32.png"

 */
