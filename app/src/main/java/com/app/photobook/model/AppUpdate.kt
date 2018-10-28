package com.app.photobook.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by shree on 10-01-2017.
 */

class AppUpdate() : Parcelable {

    @SerializedName("device")
    @Expose
    var platform: String? = null
    @SerializedName("version")
    @Expose
    var version: Int = 0
    @SerializedName("is_force_update")
    @Expose
    var forceUpdate: Boolean? = null

    constructor(parcel: Parcel) : this() {
        platform = parcel.readString()
        version = parcel.readInt()
        forceUpdate = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(platform)
        parcel.writeInt(version)
        parcel.writeValue(forceUpdate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AppUpdate> {
        override fun createFromParcel(parcel: Parcel): AppUpdate {
            return AppUpdate(parcel)
        }

        override fun newArray(size: Int): Array<AppUpdate?> {
            return arrayOfNulls(size)
        }
    }

}
