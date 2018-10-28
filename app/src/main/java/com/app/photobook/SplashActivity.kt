package com.app.photobook

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
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

        val responseBodyCall = retroApi.getPhotographerDetail("")
        responseBodyCall.enqueue(object : Callback<PhotographerRes> {
            override fun onResponse(call: Call<PhotographerRes>, response: Response<PhotographerRes>) {

                if (response.code() != 200) {
                    val res = response.errorBody().string()
                    Log.e(TAG, "onResponse: $res")
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

                    goForward(true)
                }
            }

            override fun onFailure(call: Call<PhotographerRes>, throwable: Throwable) {
                throwable.printStackTrace()
            }
        })
    }

    internal fun goForward(delayed: Boolean) {

        if (delayed) {
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
