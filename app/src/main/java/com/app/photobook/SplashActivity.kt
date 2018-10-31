package com.app.photobook

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.app.photobook.model.Maintenance
import com.app.photobook.model.PhotographerRes
import com.app.photobook.retro.RetroApi
import com.app.photobook.room.RoomDatabaseClass
import com.app.photobook.tools.MyPrefManager
import com.app.photobook.tools.Utils
import com.app.photobook.ui.MainActivity
import com.app.photobook.ui.UserProfileActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_slpash.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : AppCompatActivity() {

    internal var handler = Handler()
    internal lateinit var myPrefManager: MyPrefManager
    internal lateinit var retroApi: RetroApi
    internal lateinit var roomDatabaseClass: RoomDatabaseClass
    internal lateinit var ivSplash: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_slpash)

        myPrefManager = MyPrefManager(this)
        val app = application as CustomApp
        retroApi = app.retroApi

        roomDatabaseClass = CustomApp.getRoomDatabaseClass()

        ivSplash = findViewById(R.id.ivSplash)
        ivSplash.setImageResource(R.drawable.drawable_splash_screen)

        /*Picasso.with(this)
                .load(R.drawable.drawable_splash_screen)
                .fit()
                .into(ivSplash)*/

        /*if(BuildConfig.FLAVOR.equals("Photobook")) {
            Picasso.with(this)
                    .load(R.drawable.drawable_splash_screen)
                    .fit()
                    .into(ivSplash);
        }
        else if(BuildConfig.FLAVOR.equals("GlimpsePhotobook")) {
            Picasso.with(this)
                    .load(R.drawable.splash_screen_1)
                    .fit()
                    .into(ivSplash);
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (myPrefManager.isUserLoggedIn()) {
                    intent = new Intent(SplashActivity.this,
                            MainActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this,
                            UserProfileActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 2000);
        */

        if (!Utils.isOnline(this)) {
            goForward(false)
            return
        }

        getPhotographerInfo()
    }

    private fun getPhotographerInfo() {

        if (!Utils.isOnline(this)) return

        val responseBodyCall = retroApi.getPhotographerDetail("")
        responseBodyCall.enqueue(object : Callback<PhotographerRes> {
            override fun onResponse(call: Call<PhotographerRes>, response: Response<PhotographerRes>) {

                if (response.code() != 200) {
                    val res = response.errorBody().string()
                    Log.e(TAG, "onResponse: $res")
                    checkMaintenanceMessage()
                    return
                }

                val photographerRes = response.body()
                if (photographerRes.error == 0) {

                    //Insert Photographer
                    roomDatabaseClass.daoPhotographer().deleteAll()
                    roomDatabaseClass.daoPhotographer().insert(photographerRes.data)
                    //==//

                    if (!TextUtils.isEmpty(photographerRes.data.profilePicture)) {
                        Picasso.with(this@SplashActivity)
                                .load(photographerRes.data.profilePicture)
                                .fetch()
                    }

                    if (!TextUtils.isEmpty(photographerRes.data.logo)) {
                        Picasso.with(this@SplashActivity)
                                .load(photographerRes.data.logo)
                                .fetch()
                    }

                    checkMaintenanceMessage()
                    //goForward(true)
                } else {
                    checkMaintenanceMessage()
                }
            }

            override fun onFailure(call: Call<PhotographerRes>, throwable: Throwable) {
                throwable.printStackTrace()
                checkMaintenanceMessage()
            }
        })
    }

    private fun checkMaintenanceMessage() {

        if (!Utils.isOnline(this)) {
            goForward(true)
            return
        }

        retroApi.getMaintenance("")
                .enqueue(object : Callback<Maintenance> {
                    override fun onResponse(call: Call<Maintenance>?, response: Response<Maintenance>?) {

                        var hasForward = true

                        if (response!!.code() != 200) {
                            val res = response.errorBody().string()
                            Log.e(TAG, "onResponse: $res")
                            goForward(true)
                            return
                        }

                        var res = response.body()
                        if (res.error == 0) {
                            hasForward = if (res.data.isInMaintenance == 1) {
                                showMaintenanceDialog(res.data.message)
                                false
                            } else {
                                checkForWillMaintenance(res)
                            }
                        }

                        if (hasForward) {
                            goForward(true)
                        }
                    }

                    override fun onFailure(call: Call<Maintenance>?, t: Throwable?) {
                        t!!.printStackTrace()
                        goForward(true)
                    }
                })

    }

    /**
     * Show Will Maintenance Dialog
     */
    private fun checkForWillMaintenance(res: Maintenance): Boolean {
        return if (res.data.willMaitenance == 1) {
            var myPrefManager = MyPrefManager(this@SplashActivity)
            var maintenanceId = myPrefManager.maintenanceId
            if (maintenanceId != 0) {
                if (maintenanceId != res.data.version) {
                    showWillMaintenanceDialog(myPrefManager, res)
                    false
                } else {
                    true
                }
            } else {
                showWillMaintenanceDialog(myPrefManager, res)
                false
            }
        } else true
    }

    private fun showWillMaintenanceDialog(myPrefManager: MyPrefManager, res: Maintenance) {
        AlertDialog.Builder(this)
                .setMessage(res.data.message)
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, which ->
                    goForward(true)
                }
                .setNegativeButton("Don't show again") { dialog, which ->
                    myPrefManager.maintenanceId = res.data.version
                    goForward(true)
                }
                .show()
    }

    private fun showMaintenanceDialog(message: String) {

        AlertDialog.Builder(this)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok") { dialog, which ->
                    finish()
                }
                .show()
    }

    internal fun goForward(isDirectCall: Boolean) {

        if (isDirectCall) {
            progress.visibility = View.GONE

            val intent = if (myPrefManager.isUserLoggedIn) {
                Intent(this@SplashActivity,
                        MainActivity::class.java)
            } else {
                Intent(this@SplashActivity,
                        UserProfileActivity::class.java)
            }
            startActivity(intent)
            finish()
        } else {
            handler.postDelayed({
                goForward(true)
            }, 1000)
        }
    }

    companion object {
        var TAG = SplashActivity::class.java.simpleName
    }
}
