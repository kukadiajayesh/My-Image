package com.app.photobook

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.app.photobook.frag.FragAlbumHome
import com.app.photobook.frag.FragPhotographer
import com.app.photobook.frag.Portfolio.FragHome
import com.app.photobook.model.Photographer
import com.app.photobook.room.RoomDatabaseClass
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    internal lateinit var fragmentManager: FragmentManager
    private var mBackPressed: Long = 0

    internal lateinit var roomDatabaseClass: RoomDatabaseClass
    internal lateinit var photographer: Photographer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        fragmentManager = supportFragmentManager
        roomDatabaseClass = CustomApp.getRoomDatabaseClass()

        val fragment = FragPhotographer()
        switchFrag(fragment)

        photographer = roomDatabaseClass.daoPhotographer().clientInfo

        initBottomNavigator()
    }

    fun initBottomNavigator() {
        flHome.setOnClickListener(onClick)
        flAlbums.setOnClickListener(onClick)
        flCall.setOnClickListener(onClick)
        flPortfolio.setOnClickListener(onClick)
        //flEmail.setOnClickListener(onClick)
    }


    var onClick = View.OnClickListener { v ->
        when (v.id) {
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
            R.id.flPortfolio -> {
                var fragHome = FragHome()
                switchFrag(fragHome)

                viewSelectedHome.visibility = View.GONE
                viewSelectedAlbums.visibility = View.GONE
                viewSelectedCall.visibility = View.GONE
                viewSelectedPortfolio.visibility = View.VISIBLE
            }

        /*R.id.flEmail -> {
            Utils.sendEmail(this@MainActivity, photographer.email)

            viewSelectedHome.visibility = View.GONE
            viewSelectedAlbums.visibility = View.GONE
            viewSelectedCall.visibility = View.GONE
            viewSelectedEmail.visibility = View.VISIBLE

        }*/
            R.id.flCall -> {
                onCallBtnClick()

                viewSelectedHome.visibility = View.GONE
                viewSelectedAlbums.visibility = View.GONE
                viewSelectedCall.visibility = View.VISIBLE
                viewSelectedPortfolio.visibility = View.GONE
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
