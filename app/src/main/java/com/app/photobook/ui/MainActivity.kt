package com.app.photobook.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import com.app.photobook.CustomApp
import com.app.photobook.R
import com.app.photobook.frag.FragAlbumHome
import com.app.photobook.frag.FragPhotographer
import com.app.photobook.frag.Portfolio.FragHome
import com.app.photobook.model.Photographer
import com.app.photobook.room.RoomDatabaseClass
import com.app.photobook.tools.AppUpdateUtils
import com.app.photobook.tools.Utils
import com.takusemba.spotlight.OnTargetStateChangedListener
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.shape.Circle
import com.takusemba.spotlight.target.SimpleTarget
import com.wooplr.spotlight.SpotlightView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    internal lateinit var fragmentManager: FragmentManager
    private var mBackPressed: Long = 0

    internal lateinit var roomDatabaseClass: RoomDatabaseClass
    lateinit var photographer: Photographer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        fragmentManager = supportFragmentManager
        roomDatabaseClass = CustomApp.getRoomDatabaseClass()
        photographer = roomDatabaseClass.daoPhotographer().clientInfo

        if (Utils.isOnline(this)) {
            selectBottomMenu(R.id.flPortfolio)
        } else {
            selectBottomMenu(R.id.flHome)
        }

        initBottomNavigator()

        //App Version Checking
        AppUpdateUtils(this, retroApi, myPrefManager).fetchJson()

        /*flAlbums.postDelayed({
            showHelp()
        }, 500)*/

        flAlbums.postDelayed({
            showHelp()
        }, 1000)

    }

    fun initBottomNavigator() {
        flHome.setOnClickListener(onClick)
        flAlbums.setOnClickListener(onClick)
        flCall.setOnClickListener(onClick)
        flPortfolio.setOnClickListener(onClick)
        //flEmail.setOnClickListener(onClick)
    }

    var onClick = View.OnClickListener { v ->
        selectBottomMenu(v.id)
    }

    fun selectBottomMenu(id: Int) {

        when (id) {
            R.id.flPortfolio -> {
                var fragHome = FragHome()
                switchFrag(fragHome)

                viewSelectedHome.visibility = View.GONE
                viewSelectedAlbums.visibility = View.GONE
                viewSelectedCall.visibility = View.GONE
                viewSelectedPortfolio.visibility = View.VISIBLE
            }
            R.id.flHome -> {
                var fragPhotographer = FragPhotographer()
                switchFrag(fragPhotographer)

                viewSelectedHome.visibility = View.VISIBLE
                viewSelectedAlbums.visibility = View.GONE
                viewSelectedCall.visibility = View.GONE
                viewSelectedPortfolio.visibility = View.GONE
            }
            R.id.flAlbums -> {
                var fragAlbums = FragAlbumHome()
                switchFrag(fragAlbums)

                viewSelectedHome.visibility = View.GONE
                viewSelectedAlbums.visibility = View.VISIBLE
                viewSelectedCall.visibility = View.GONE
                viewSelectedPortfolio.visibility = View.GONE
            }

            /*R.id.flEmail -> {
                Utils.sendEmail(this@MainActivity, photographer.email)

                viewSelectedHome.visibility = View.GONE
                viewSelectedAlbums.visibility = View.GONE
                viewSelectedCall.visibility = View.GONE
                viewSelectedEmail.visibility = View.VISIBLE

            }*/
            R.id.flCall -> {

                if (TextUtils.isEmpty(photographer.mobile)) {
                    Toast.makeText(this, R.string.message_call_string_empty, Toast.LENGTH_SHORT).show()
                    return
                }

                AlertDialog.Builder(this)
                        .setMessage(photographer.mobile)
                        .setPositiveButton("Call") { dialog, which ->
                            onCallBtnClick()

                            viewSelectedHome.visibility = View.GONE
                            viewSelectedAlbums.visibility = View.GONE
                            viewSelectedCall.visibility = View.VISIBLE
                            viewSelectedPortfolio.visibility = View.GONE
                        }
                        .setNegativeButton("Cancel", null)
                        .show()

            }

        }

    }

    private fun onCallBtnClick() {
        if (Build.VERSION.SDK_INT < 23) {
            phoneCall()
        } else {

            if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                phoneCall()
            } else {
                val PERMISSIONS_STORAGE = arrayOf<String>(Manifest.permission.CALL_PHONE)
                //Asking request Permissions
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 9)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {

            9 -> {
                var permissionGranted = false
                when (requestCode) {
                    9 -> permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                }
                if (permissionGranted) {
                    phoneCall()
                } else {
                    Toast.makeText(this, "You don't assign permission.", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

    private fun phoneCall() {
        if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            val callIntent = Intent(Intent.ACTION_CALL)

            var mobile = photographer.mobile
            if (!TextUtils.isEmpty(mobile)) {
                callIntent.data = Uri.parse("tel:" + photographer.mobile)
                if (packageManager.resolveActivity(callIntent, 0) != null) {
                    startActivity(callIntent)
                } else {
                    Toast.makeText(this, "Unable to find calling app", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Mobile is empty", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(this, "You don't assign permission.", Toast.LENGTH_SHORT).show()
        }
    }

    internal fun switchFrag(fragment: Fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.llContainer, fragment)
                .commit()
    }

    fun showHelp2() {

        val simpleTarget = SimpleTarget.Builder(this)
                .setPoint(flAlbums)
                .setShape(Circle(200f))
                .setTitle("the title")
                .setDescription("the description")
                .setOnSpotlightStartedListener(object : OnTargetStateChangedListener<SimpleTarget> {
                    override fun onStarted(target: SimpleTarget) {
                    }

                    override fun onEnded(target: SimpleTarget) {
                    }
                })
                .build()

        Spotlight.with(this)
                .setOverlayColor(R.color.background)
                .setDuration(1000L)
                .setAnimation(DecelerateInterpolator(2f))
                .setTargets(simpleTarget)
                .setClosedOnTouchedOutside(true)
                .start()
    }

    fun showHelp() {

        SpotlightView.Builder(this)
                .introAnimationDuration(400)
                .enableRevealAnimation(true)
                .performClick(true)
                .fadeinTextDuration(400)
                .headingTvColor(Color.parseColor("#eb273f"))
                .headingTvSize(32)
                .headingTvText(getString(R.string.showcase_add_button_title))
                .subHeadingTvColor(Color.parseColor("#ffffff"))
                .subHeadingTvSize(16)
                .subHeadingTvText(getString(R.string.showcase_add_button))
                .maskColor(Color.parseColor("#dc000000"))
                .target(findViewById(R.id.flAlbums))
                .lineAnimDuration(400)
                .lineAndArcColor(Color.parseColor("#eb273f"))
                .dismissOnTouch(true)
                .dismissOnBackPress(true)
                .enableDismissAfterShown(true)
                .usageId("1") //UNIQUE ID
                .show()
    }

    override fun onBackPressed() {

        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
            Toast.makeText(baseContext, "Touch again to exit", Toast.LENGTH_SHORT).show()
        }
        mBackPressed = System.currentTimeMillis()
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private val TIME_INTERVAL = 2000 // # milliseconds, desired time passed between two back presses.
    }
}
