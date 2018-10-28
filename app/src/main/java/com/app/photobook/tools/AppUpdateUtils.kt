package com.app.photobook.tools

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.util.Log
import com.app.photobook.model.AppUpdate
import com.app.photobook.model.AppUpdateRes
import com.app.photobook.retro.RetroApi
import com.app.photobook.ui.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AppUpdateUtils(var context: MainActivity, var retroApi: RetroApi, var myPrefManager: MyPrefManager) {

    private val TAG = AppUpdateUtils::class.java.simpleName
    private var updateList = ArrayList<AppUpdate>()

    private var deviceType = "android"

    fun fetchJson() {

        if (!Utils.isOnline(context)) {
            return
        }

        retroApi.getCheckVersion(Constants.APP_TYPE).enqueue(object : Callback<AppUpdateRes> {
            override fun onResponse(call: Call<AppUpdateRes>?, response: Response<AppUpdateRes>?) {

                if (response!!.code() != 200) {
                    val res = response.errorBody().string()
                    Log.e(TAG, "onResponse: $res")
                    return
                }

                val json = response.body()
                if (json.error == Constants.RESPONSE_STATUS_OK) {

                    updateList.addAll(json.data!!)
                    if (updateList.isNotEmpty()) {
                        updateApp(updateList)
                    }
                }
            }

            override fun onFailure(call: Call<AppUpdateRes>?, t: Throwable?) {
                t!!.printStackTrace()
            }
        })
    }


    private fun updateApp(newsArrayListAppUpdate: ArrayList<AppUpdate>) {

        val manager = context.packageManager
        var info: PackageInfo? = null
        try {
            info = manager.getPackageInfo(
                    context.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        val version = info!!.versionCode
        var skipLoop = false
        var finalVersion = 0

        newsArrayListAppUpdate.forEach { appUpdate ->

            if (version < appUpdate.version &&
                    appUpdate.platform == deviceType) {

                if (appUpdate.forceUpdate!!) {
                    finallyUpdate()
                    return
                } else {
                    if (myPrefManager.laterVersion < appUpdate.version && !skipLoop) {
                        finalVersion = appUpdate.version
                    }
                }
            }
        }

        if (finalVersion != 0) {
            update(finalVersion)
        }
    }

    private fun update(versionCode: Int) {

        val aBuilder = AlertDialog.Builder(context)
        aBuilder
                .setMessage("New Version is available, do you want to update?")
                .setPositiveButton("Update") { dialog, which ->

                    val appPackageName = context.packageName // getPackageName() from Context or Activity object
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=$appPackageName")))
                    } catch (anfe: android.content.ActivityNotFoundException) {
                        context.startActivity(Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                    }

                    myPrefManager.setLaterVersionCode(versionCode)
                    context.finish()
                }
                .setNegativeButton("No, Thanks") { dialog, which ->
                    myPrefManager.setLaterVersionCode(versionCode)
                }

        val alertDialog = aBuilder.create()
        alertDialog.show()
    }

    private fun finallyUpdate() {

        val aBuilder = AlertDialog.Builder(context)
        aBuilder.setTitle("Update Required")
                .setCancelable(false)
                .setMessage("An important new version of App is available. You need to update before you can continue using App.")
                .setPositiveButton("Update") { dialog, which ->
                    val appPackageName = context.packageName // getPackageName() from Context or Activity object
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=$appPackageName")))
                    } catch (anfe: android.content.ActivityNotFoundException) {
                        context.startActivity(Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                    }
                    context.finish()
                }

        val alertDialog = aBuilder.create()
        alertDialog.show()
    }
}