package com.app.photobook.tools

import android.view.ViewGroup


class ViewUtils {
    fun enableViews(viewGroup: ViewGroup, value: Boolean) {
        for (i in 0 until viewGroup.childCount) {
            val view = viewGroup.getChildAt(i)
            view.isEnabled = value // Or whatever you want to do with the view.
        }
    }
}