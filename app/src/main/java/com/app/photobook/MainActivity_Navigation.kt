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
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.widget.Toast
import com.app.photobook.frag.FragAlbumHome
import com.app.photobook.frag.FragPhotographer
import com.app.photobook.model.Photographer
import com.app.photobook.room.RoomDatabaseClass
import com.app.photobook.tools.Utils
import com.luseen.luseenbottomnavigation.BottomNavigation.BottomNavigationItem
import com.luseen.luseenbottomnavigation.BottomNavigation.OnBottomNavigationItemClickListener
import kotlinx.android.synthetic.main.activity_main_navigation.*


class MainActivity_Navigation : BaseActivity() {

    internal lateinit var fragmentManager: FragmentManager
    private var mBackPressed: Long = 0

    internal lateinit var roomDatabaseClass: RoomDatabaseClass
    internal lateinit var photographer: Photographer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_navigation)

        fragmentManager = supportFragmentManager
        roomDatabaseClass = CustomApp.getRoomDatabaseClass()

        val fragment = FragPhotographer()
        switchFrag(fragment)

        photographer = roomDatabaseClass.daoPhotographer().clientInfo

        initBottomNavigator()
    }

    fun initBottomNavigator() {

        var itemHome = BottomNavigationItem(getString(R.string.menu_title_home),
                ContextCompat.getColor(this, R.color.bottomColor), R.drawable.ic_menu_home)
        bottomNavigation.addTab(itemHome)

        var itemAlbum = BottomNavigationItem(getString(R.string.menu_title_album),
                ContextCompat.getColor(this, R.color.bottomColor), R.drawable.ic_menu_album)
        bottomNavigation.addTab(itemAlbum)

        var itemContact = BottomNavigationItem(getString(R.string.menu_title_call),
                ContextCompat.getColor(this, R.color.bottomColor), R.drawable.ic_menu_call)
        bottomNavigation.addTab(itemContact)

        var itemEmail = BottomNavigationItem(getString(R.string.menu_title_email),
                ContextCompat.getColor(this, R.color.bottomColor), R.drawable.ic_menu_email)
        bottomNavigation.addTab(itemEmail)

        bottomNavigation.setItemActiveColorWithoutColoredBackground(ContextCompat.getColor(this, R.color.bottomTextColor))
        bottomNavigation.setOnBottomNavigationItemClickListener(onBottomNavigationItemClickListener)
    }

    private var onBottomNavigationItemClickListener = OnBottomNavigationItemClickListener { index ->
        when (index) {
            0 -> {
                var fragPhotographer = FragPhotographer()
                switchFrag(fragPhotographer)
            }
            1 -> {
                var fragAlbums = FragAlbumHome()
                switchFrag(fragAlbums)
            }
            2 -> {
                onCallBtnClick()
            }
            3 -> {
                Utils.sendEmail(this@MainActivity_Navigation, photographer.email)
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

    private fun phoneCall() {
        if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            val callIntent = Intent(Intent.ACTION_CALL)

            var mobile = photographer.mobile
            if (!TextUtils.isEmpty(mobile)) {
                callIntent.data = Uri.parse(photographer.mobile)
                startActivity(callIntent)
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
        private val TAG = MainActivity_Navigation::class.java.simpleName
        private val TIME_INTERVAL = 2000 // # milliseconds, desired time passed between two back presses.
    }
}
