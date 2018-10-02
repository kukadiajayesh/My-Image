package com.app.photobook.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import butterknife.ButterKnife
import com.app.photobook.CustomApp
import com.app.photobook.R
import com.app.photobook.dialog.CommentDialog
import com.app.photobook.helper.PhotoSelectionUtils
import com.app.photobook.model.Album
import com.app.photobook.model.AlbumImage
import com.app.photobook.retro.RetroApi
import com.app.photobook.room.RoomDatabaseClass
import com.app.photobook.tools.Constants
import com.app.photobook.tools.SharingUtils
import kotlinx.android.synthetic.main.activity_photopreview.*
import kotlinx.android.synthetic.main.navigation_toolbar.*
import java.util.*

class PhotoPreviewActivity : AppCompatActivity() {

    internal lateinit var album: Album
    protected var albumImages: List<AlbumImage>? = null
    protected var current: Int = 0

    internal lateinit var retroApi: RetroApi
    protected lateinit var context: Context
    internal lateinit var commentDialog: CommentDialog
    internal lateinit var roomDatabaseClass: RoomDatabaseClass
    internal var hasSelectionChanged = false
    internal var handler = Handler()
    internal lateinit var photoSelectionUtils: PhotoSelectionUtils
    lateinit var viewPager: ViewPager
    private var viewMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //BigImageViewer.initialize(GlideImageLoader.with(getApplicationContext()));

        setContentView(R.layout.activity_photopreview)
        ButterKnife.bind(this)

        context = this@PhotoPreviewActivity

        val app = application as CustomApp
        retroApi = app.retroApi

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        //toolbar!!.title = (current + 1).toString() + "/" + albumImages!!.size

        roomDatabaseClass = CustomApp.getRoomDatabaseClass()
        commentDialog = CommentDialog(this, retroApi, dialogLayout as LinearLayout)

        val mBundle = intent.extras
        current = mBundle!!.getInt("key_pos")
        album = mBundle.getParcelable("album")
        albumImages = mBundle.getParcelableArrayList("albumImages")
        viewMode = mBundle.getBoolean("view_mode")

        photoSelectionUtils = PhotoSelectionUtils(this, roomDatabaseClass,
                album, albumImages as ArrayList<AlbumImage>?)

        viewPager = view_pager
        //overridePendingTransition(R.anim.activity_alpha_action_in, 0)
        bindData()

        handler.postDelayed({
            toolbar!!.title = (current + 1).toString() + "/" + albumImages!!.size
        }, 100)

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (viewMode) {
            return super.onCreateOptionsMenu(menu)
        }

        menuInflater.inflate(R.menu.menu_comment, menu)
        var menuSelection = menu.findItem(R.id.action_select)

        if (album.eventType == Constants.GALLERY_TYPE_SELECTION) {

            var albumImage = albumImages!![view_pager.currentItem]
            if (albumImage.selected) {
                menuSelection.setIcon(R.drawable.ic_check_box)
            } else {
                menuSelection.setIcon(R.drawable.ic_check_box_empty)
            }

        } else {
            menuSelection.isVisible = false
        }


        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.action_select -> {

                var albumImage = albumImages!![view_pager.currentItem]
                var value = !albumImage.selected

                if (value && !photoSelectionUtils.validateSelection()) {
                    return super.onOptionsItemSelected(item)
                }

                albumImage.selected = !albumImage.selected
                roomDatabaseClass.daoAlbumImage().update(albumImage)
                hasSelectionChanged = true
                invalidateOptionsMenu()
            }
            R.id.action_comment -> {
                var albumImage = albumImages!![view_pager.currentItem]
                commentDialog.show(albumImage.albumId, albumImage.pageId)
            }
            R.id.action_share -> {
                var albumImage = albumImages!![view_pager.currentItem]

                val uri = Uri.parse(albumImage.localFilePath)
                SharingUtils.shareAlbum(context, "", uri)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    protected fun bindData() {
        //view_pager!!.adapter = statePagerAdapter
        view_pager!!.adapter = ViewPagerAdapter(view_pager, albumImages!!, viewMode)
        view_pager!!.setCurrentItem(current, true)
        view_pager.pageMargin = resources.getDimensionPixelSize(R.dimen.view_pager_margin)

        view_pager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                current = position
                updatePercent()
                invalidateOptionsMenu()
            }
        })


        toolbar!!.title = (current + 1).toString() + "/" + albumImages!!.size
    }

    private val statePagerAdapter = object : FragmentStatePagerAdapter(supportFragmentManager) {

        override fun getItem(position: Int): Fragment {
            val bundle = Bundle()
            bundle.putString("path", albumImages!![position].localFilePath) //"file://" +
            return FragPhotoPreview.getInstant(bundle)
        }

        override fun getCount(): Int {
            return if (albumImages == null) {
                0
            } else {
                albumImages!!.size
            }
        }
    }

/*
    override fun onPageScrollStateChanged(arg0: Int) {

    }

    override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {

    }

    override fun onPageSelected(arg0: Int) {
        current = arg0
        updatePercent()
    }*/

    protected fun updatePercent() {
        toolbar!!.title = (current + 1).toString() + "/" + albumImages!!.size
    }

    override fun onDestroy() {
        super.onDestroy()
        if (hasSelectionChanged) {
            LocalBroadcastManager.getInstance(this)
                    .sendBroadcast(Intent(Constants.ACTION_UPDATE_ALBUM_SELECTION))
        }
    }

    companion object {
        private val TAG = PhotoPreviewActivity::class.java.simpleName
    }


}
