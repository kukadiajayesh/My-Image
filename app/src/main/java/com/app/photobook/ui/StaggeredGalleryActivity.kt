package com.app.photobook.ui

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.app.photobook.CustomApp
import com.app.photobook.R
import com.app.photobook.R.id.toolbar
import com.app.photobook.adapter.GalleryAdapter
import com.app.photobook.dialog.CommentDialog
import com.app.photobook.helper.PhotoSelectionUtils
import com.app.photobook.model.Album
import com.app.photobook.model.AlbumImage
import com.app.photobook.model.User
import com.app.photobook.retro.RetroApi
import com.app.photobook.room.RoomDatabaseClass
import com.app.photobook.tools.Constants
import com.app.photobook.tools.MyPrefManager
import com.app.photobook.tools.ShowcaseUtils
import com.app.photobook.tools.Utils
import com.wooplr.spotlight.utils.SpotlightListener
import kotlinx.android.synthetic.main.activity_gallery.*
import kotlinx.android.synthetic.main.navigation_toolbar.*
import okhttp3.ResponseBody
import org.apache.http.HttpStatus
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.util.*

class StaggeredGalleryActivity : AppCompatActivity() {

    internal lateinit var retroApi: RetroApi

    internal lateinit var progressDialog: ProgressDialog
    internal lateinit var album: Album
    internal lateinit var albumImages: ArrayList<AlbumImage>
    internal var albumImagesTemp = ArrayList<AlbumImage>()

    internal lateinit var myPrefManager: MyPrefManager
    internal var user: User? = null

    private var galleryAdapter: GalleryAdapter? = null
    var hasSelectionChanged = false
    var showMenu = false
    var liveMode = false
    var isOffline = true

    internal lateinit var commentDialog: CommentDialog
    internal lateinit var roomDatabaseClass: RoomDatabaseClass
    internal lateinit var photoSelectionUtils: PhotoSelectionUtils
    var totalSelected = 0
    var viewSelected = false
    internal var ivTitleIcon: ImageView? = null

    private val MIN_CLICK_INTERVAL: Long = 400
    private var mLastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_gallery)

        if (intent.hasExtra("album")) {
            album = intent.getParcelableExtra("album")
            albumImages = album.images
            albumImagesTemp.addAll(albumImages)

            liveMode = intent.getBooleanExtra("live_mode", false)
            isOffline = album.isOffline == 1

            //Check whether to any images selected or not
            checkMenuVisibility()
        }

        myPrefManager = MyPrefManager(this)
        user = myPrefManager.userDetails

        val app = application as CustomApp
        retroApi = app.retroApi

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("")
        progressDialog.setMessage("Please Wait...")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setCancelable(true)

        initializeActionBar()
        initializeView()
        setBroadcast()
        getSelectionEvent()
        //sdfsd()

        if (liveMode) {
            llSelectionFooter.visibility = View.GONE
            tvViewSelected.visibility = View.GONE
        }

        if (album.eventType != Constants.GALLERY_TYPE_SELECTION) {
            tvSelectionCounter.visibility = View.INVISIBLE
            tvViewSelected.visibility = View.GONE
        }

        tvTotal.text = "Total : " + album.images.size.toString()

        commentDialog = CommentDialog(this, retroApi, dialogLayout as LinearLayout)
        roomDatabaseClass = CustomApp.getRoomDatabaseClass()
        photoSelectionUtils = PhotoSelectionUtils(this, roomDatabaseClass, album, albumImages)

        tvViewSelected.setOnClickListener(onClick)

        if (!liveMode) {
            if (album.eventType == Constants.GALLERY_TYPE_SELECTION) {
                tvViewSelected.postDelayed({
                    with(ShowcaseUtils(this)) {
                        showInGallerySelectionScreen(tvViewSelected, spotlightListener)
                    }
                }, 1000)
            }
        }
    }

    var spotlightListener = SpotlightListener {

        if (it == ShowcaseUtils.UNIQUE_ID_GALLERY_SELECTION) {
            if (ivTitleIcon != null) {
                ShowcaseUtils(this).showInGallerySubmitScreen(ivTitleIcon!!)
            }
        }
    }

    private fun sdfsd() {
        var handler = Handler()
        handler.postDelayed({
            updateMaxEventSelectionLimit(5)
        }, 2000)
    }

    private fun checkMenuVisibility() {
        albumImages.forEach { img ->
            if (img.selected) {
                showMenu = true
            }
        }
    }

    private fun updateImageFromDb() {
        if (!liveMode) {
            var myAlbumImages = roomDatabaseClass.daoAlbumImage().getAllAlbums(album.id) as ArrayList<AlbumImage>
            albumImages.clear()
            albumImages.addAll(myAlbumImages)
            checkMenuVisibility()
            galleryAdapter!!.notifyDataSetChanged()
            invalidateOptionsMenu()
            photoSelectionUtils = PhotoSelectionUtils(this, roomDatabaseClass, album, albumImages)
            updateCounter()
        }
    }

    private fun updateCounter() {
        totalSelected = 0
        albumImages.forEach { item ->
            if (item.selected) {
                totalSelected++
            }
        }
    }

    private fun initializeActionBar() {
        /*if (liveMode) {
            toolbar!!.title = album.eventName + " (" + album.images.size + ")"
        } else {

        }
*/
        toolbar!!.title = album.eventName
        //toolbar!!.title = album.eventName + " (" + album.images.size + ")"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun initializeView() {

        recyclerView!!.setHasFixedSize(true)
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        /*layoutManager.setGapStrategy(
                StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);*/

        recyclerView!!.layoutManager = layoutManager
        galleryAdapter = GalleryAdapter(this, album, albumImages)
        recyclerView!!.adapter = galleryAdapter

        /*
        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent mIntent = new Intent(StaggeredGalleryActivity.this, PhotoPreviewActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putInt("Key_FolderID", AlbummID);
                mBundle.putInt("Key_ID", position);
                mIntent.putExtras(mBundle);
                startActivity(mIntent);
            }
        });*/

    }

    fun validClick(): Boolean {
        val currentClickTime = SystemClock.uptimeMillis()
        val elapsedTime = currentClickTime - mLastClickTime

        if (elapsedTime <= MIN_CLICK_INTERVAL)
            return false

        mLastClickTime = currentClickTime
        return true
    }

    var onClick = View.OnClickListener { view ->
        when (view.id) {
            R.id.tvViewSelected -> {

                if (!validClick()) {
                    return@OnClickListener
                }

                albumImages.clear()

                if (viewSelected) {
                    tvViewSelected.setText(R.string.text_view_selected)
                    albumImages.addAll(albumImagesTemp)
                } else {
                    tvViewSelected.setText(R.string.text_view_all)
                    albumImages.addAll(albumImagesTemp.filter {
                        it.selected
                    } as ArrayList<AlbumImage>)
                }

                viewSelected = !viewSelected
                galleryAdapter!!.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        /*if (showMenu) {
            menuInflater.inflate(R.menu.menu_submit, menu)
        }*/
        if (!liveMode) {
            menuInflater.inflate(R.menu.menu_submit, menu)
            var menuSubmit = menu!!.findItem(R.id.action_submit)
            menuSubmit.actionView.setOnClickListener { onOptionsItemSelected(menuSubmit) }
            menuSubmit.isVisible = album.eventType == Constants.GALLERY_TYPE_SELECTION
            updateCounter()
            //toolbar!!.title = album.eventName + " (" + totalSelected.toString() + " / " + album.images.size + ")"
            tvSelectionCounter.text = "Selection:  " + totalSelected.toString() + " / " + album.eventMaximumSelect

            ivTitleIcon = menuSubmit.actionView.findViewById(R.id.ivIcon)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.action_submit -> {
                var ids = photoSelectionUtils.getSelectedIds()
                promptSubmit(ids)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun promptSubmit(ids: String) {
        val builder1 = AlertDialog.Builder(this@StaggeredGalleryActivity)
        builder1.setTitle("Are you ready to submit?")
        builder1.setMessage("If it is OK to proceed, click the Submit button. \n\n" +
                "Once you submit, you will no longer be able to change your selection and your current choice will be sent to photographer. \n\n" +
                "Note you don't have to submit until you are happy with your choice. Your work so far is saved and you can always come back another day to finish later!")
        builder1.setCancelable(false)
        builder1.setPositiveButton(
                "Submit") { dialog, id ->
            dialog.dismiss()
            submitSelectedPhotos(ids)
        }

        builder1.setNegativeButton(
                "Cancel"
        ) { dialog, id -> dialog.dismiss() }

        val alert11 = builder1.create()
        alert11.show()
    }

    fun submitSelectedPhotos(ids: String) {

        if (!Utils.isOnline(this)) {
            Utils.showNoInternetMessage(this, recyclerView)
            progressDialog.dismiss()
            return
        }

        progressDialog.setMessage("Please wait...")
        progressDialog.show()

        Log.e(TAG, ids)

        val responseBodyCall = retroApi.submitImages(user!!.id.toString(), ids, album.id.toString())
        responseBodyCall.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
                try {
                    if (response.code() == HttpStatus.SC_OK) {
                        var res = response.body().string()

                        var json = JSONObject(res)
                        var error = json.getInt("error")
                        var msg = json.getString("msg")

                        if (error == 0) {

                        }

                        Utils.showDialog(this@StaggeredGalleryActivity, "", msg, null)
                        //Toast.makeText(this@StaggeredGalleryActivity, msg, Toast.LENGTH_LONG).show()

                    } else {
                        var res = response.errorBody().string()
                        Log.e("", res)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                progressDialog.dismiss()
            }
        }
        )
    }


    private fun getSelectionEvent() {

        if (!Utils.isOnline(this)) return

        val responseBodyCall = retroApi.getMaxSelection(user!!.id.toString(), album.id.toString())
        responseBodyCall.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    if (response.code() == HttpStatus.SC_OK) {
                        var res = response.body().string()

                        var json = JSONObject(res)
                        var error = json.getInt("error")
                        var msg = json.getString("msg")

                        if (error == 0) {
                            var data = json.getJSONObject("data")
                            var maxLimit = data.getInt("event_maximum_select")
                            if (album.eventMaximumSelect != maxLimit)
                                updateMaxEventSelectionLimit(maxLimit)
                        } else {
                            Toast.makeText(this@StaggeredGalleryActivity, msg, Toast.LENGTH_LONG).show()
                        }

                        //Toast.makeText(this@StaggeredGalleryActivity, msg, Toast.LENGTH_LONG).show()

                    } else {
                        var res = response.errorBody().string()
                        Log.e("", res)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                t!!.printStackTrace()
            }
        }
        )
    }

    private fun updateMaxEventSelectionLimit(maxLimit: Int) {
        album.eventMaximumSelect = maxLimit
        roomDatabaseClass.daoAlbum().update(album)

        //Notify
        photoSelectionUtils.setMaxEventSelection(maxLimit)
        hasSelectionChanged = true

        tvSelectionCounter.text = "Selection:  " + totalSelected.toString() + " / " + album.eventMaximumSelect

        /*Utils.showDialog(this@StaggeredGalleryActivity, getString(R.string.app_name),
                getString(R.string.message_error_max_selection), null)*/
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        /*if (resultCode == RESULT_OK) {
            if (requestCode == UCrop.REQUEST_CROP) {
                //handleCropResult(data);
                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra(UCrop.EXTRA_INPUT_URI, data.getParcelableExtra(UCrop.EXTRA_INPUT_URI));
                intent.putExtra(UCrop.EXTRA_OUTPUT_URI, data.getParcelableExtra(UCrop.EXTRA_OUTPUT_URI));
                intent.putExtra("time", (new Date().getTime() / 1000));
                startActivity(intent);
            }
        }
        if (resultCode == UCrop.RESULT_ERROR) {
            handleCropError(data);
        }*/
    }

    /*private void LoadAllAlbum() {
        if (galleryAdapter != null) {
            galleryAdapter.updateAlbums(photos);
        }
    }*/

    fun setBroadcast() {
        var intentFilter = IntentFilter(Constants.ACTION_UPDATE_ALBUM_SELECTION)
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(broadcastReceiver, intentFilter)
    }

    var broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent!!.action == Constants.ACTION_UPDATE_ALBUM_SELECTION) {
                hasSelectionChanged = true
                updateImageFromDb()
            }
        }
    }

    public override fun onDestroy() {

        if (hasSelectionChanged) {
            LocalBroadcastManager.getInstance(this)
                    .sendBroadcast(Intent(Constants.ACTION_UPDATE_ALBUMS))
        }

        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(broadcastReceiver)

        super.onDestroy()
    }

    companion object {
        private val TAG = StaggeredGalleryActivity::class.java.simpleName
    }
}
