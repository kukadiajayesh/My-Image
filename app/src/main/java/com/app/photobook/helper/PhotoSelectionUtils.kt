package com.app.photobook.helper

import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import com.app.photobook.R
import com.app.photobook.model.Album
import com.app.photobook.model.AlbumImage
import com.app.photobook.room.RoomDatabaseClass
import com.app.photobook.tools.Constants
import com.app.photobook.tools.Utils
import java.util.*

class PhotoSelectionUtils(var context: Context,
                          var roomDatabaseClass: RoomDatabaseClass,
                          var album: Album,
                          var albumImages: ArrayList<AlbumImage>?) {

    private var eventMaximumSelect = album.eventMaximumSelect
    //var tempValue = 18
    var photographer = roomDatabaseClass.daoPhotographer().clientInfo

    fun validateSelection(): Boolean {

        if (eventMaximumSelect >= Constants.MAX_DISPLAY_VALUE) {
            if (getSelectedIdsCount() == (eventMaximumSelect - Constants.MAX_DISPLAY_VALUE)) {
                Toast.makeText(context, "You have remaining " + Constants.MAX_DISPLAY_VALUE +
                        " images to select", Toast.LENGTH_LONG).show()
            }
        }

        if (getSelectedIdsCount() >= eventMaximumSelect) {
            var msg = context.getString(R.string.message_no_limit_reached)

            var mobile = ""
            if (!TextUtils.isEmpty(photographer.mobile)) {
                mobile = photographer.mobile
            }
            msg = msg.replace("#mobile_no", mobile)
            msg = msg.replace("#limit", eventMaximumSelect.toString())

            Utils.showDialog(context, context.getString(R.string.app_name),
                    msg, null)
            /*Toast.makeText(staggeredGalleryActivity, staggeredGalleryActivity.getString(R.string.message_no_limit_reached),
                    Toast.LENGTH_LONG).show()*/
            return false
        }
        return true
    }

    fun getSelectedIds(): String {
        var ids = StringBuffer()
        for (images in albumImages!!) {
            if (images.selected) {
                if (ids.isEmpty()) ids.append(images.id) else ids.append("," + images.id)
            }
        }
        return ids.toString()
    }

    fun getSelectedIdsCount(): Int {
        var counter = 0
        for (images in albumImages!!) {
            if (images.selected) {
                counter++
            }
        }
        return counter
    }

    fun setMaxEventSelection(value: Int) {
        this.eventMaximumSelect = value
    }
}
