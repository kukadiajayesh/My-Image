package com.app.photobook.tools

import android.app.Activity
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.view.View
import com.app.photobook.R
import com.wooplr.spotlight.SpotlightView
import com.wooplr.spotlight.utils.SpotlightListener

class ShowcaseUtils(var activity: Activity) {

    companion object {
        var UNIQUE_ID_HOME_SCREEN = "1"
        var UNIQUE_ID_ALBUM_ADD = "2"
        var UNIQUE_ID_GALLERY_SELECTION = "33"
        var UNIQUE_ID_GALLERY_SUBMIT = "44"
    }

    fun showInHomeScreen(view: View, spotlightListener: SpotlightListener? = null) {
        show(view, activity.getString(R.string.showcase_add_button_title),
                activity.getString(R.string.showcase_add_button), UNIQUE_ID_HOME_SCREEN, spotlightListener)
    }

    fun showInAlbumAddScreen(view: View, spotlightListener: SpotlightListener? = null) {
        show(view, activity.getString(R.string.showcase_add_button_title),
                activity.getString(R.string.showcase_add_button), UNIQUE_ID_ALBUM_ADD, spotlightListener)
    }

    fun showInGallerySelectionScreen(view: View, spotlightListener: SpotlightListener? = null) {
        show(view, activity.getString(R.string.showcase_image_selection_button_title),
                activity.getString(R.string.showcase_image_selection_button_message),
                UNIQUE_ID_GALLERY_SELECTION, spotlightListener, 20)
    }

    fun showInGallerySubmitScreen(view: View, spotlightListener: SpotlightListener? = null) {
        show(view, activity.getString(R.string.showcase_submit_image_button_title),
                activity.getString(R.string.showcase_submit_image_button_message),
                UNIQUE_ID_GALLERY_SUBMIT, spotlightListener)
    }


    private fun show(view: View, title: String, message: String, uniqueId: String,
                     spotlightListener: SpotlightListener? = null, headerTextSize: Int = 32) {

        var spotLight = SpotlightView.Builder(activity)
                .introAnimationDuration(400)
                .enableRevealAnimation(true)
                .performClick(true)
                .fadeinTextDuration(400)
                .headingTvColor(ContextCompat.getColor(activity, R.color.colorPrimary))
                .headingTvSize(headerTextSize)
                .headingTvText(title)
                .subHeadingTvColor(Color.parseColor("#ffffff"))
                .subHeadingTvSize(16)
                .subHeadingTvText(message)
                .maskColor(Color.parseColor("#dc000000"))
                .target(view)
                .lineAnimDuration(400)
                .lineAndArcColor(ContextCompat.getColor(activity, R.color.colorPrimary))
                .dismissOnTouch(true)
                .dismissOnBackPress(true)
                .enableDismissAfterShown(true)
                .usageId(uniqueId) //UNIQUE ID

        if (spotlightListener != null) {
            spotLight.setListener(spotlightListener)
        }
        spotLight.show()

    }
}