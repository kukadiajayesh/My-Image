package com.app.photobook.ui

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import com.app.photobook.CustomApp
import com.app.photobook.R
import com.app.photobook.adapter.GalleryAdapter
import com.app.photobook.dialog.CommentDialog
import com.app.photobook.helper.PhotoSelectionUtils
import com.app.photobook.model.Album
import com.app.photobook.model.AlbumImage
import com.app.photobook.retro.RetroApi
import com.app.photobook.room.RoomDatabaseClass
import com.app.photobook.tools.Constants
import com.app.photobook.tools.Utils
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

    private var galleryAdapter: GalleryAdapter? = null
    var hasSelectionChanged = false
    var showMenu = false
    var liveMode = false

    internal lateinit var commentDialog: CommentDialog
    internal lateinit var roomDatabaseClass: RoomDatabaseClass
    internal lateinit var photoSelectionUtils: PhotoSelectionUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_gallery)

        if (intent.hasExtra("album")) {
            album = intent.getParcelableExtra("album")
            albumImages = album.images

            liveMode = intent.getBooleanExtra("live_mode", false)
            //Check whether to any images selected or not
            checkMenuVisibility()
        }

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
        //getSelectionEvent()
        //sdfsd()

        commentDialog = CommentDialog(this, retroApi, dialogLayout as LinearLayout)
        roomDatabaseClass = CustomApp.getRoomDatabaseClass()
        photoSelectionUtils = PhotoSelectionUtils(this, roomDatabaseClass, album, albumImages)
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
        }
    }

    private fun initializeActionBar() {
        toolbar!!.title = album.eventName + " (" + album.images.size + ")"
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


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        /*if (showMenu) {
            menuInflater.inflate(R.menu.menu_submit, menu)
        }*/
        if (!liveMode) {
            menuInflater.inflate(R.menu.menu_submit, menu)
            var menuSubmit = menu!!.findItem(R.id.action_submit)
            menuSubmit.actionView.setOnClickListener { onOptionsItemSelected(menuSubmit) }
            menuSubmit.isVisible = album.eventType == Constants.GALLERY_TYPE_SELECTION
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
        builder1.setMessage("Are you sure want to submit the selection?")
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

        val responseBodyCall = retroApi.submitImages(getString(R.string.photographer_id), ids, album.id.toString())
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

                        Toast.makeText(this@StaggeredGalleryActivity, msg, Toast.LENGTH_LONG).show()

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

        val responseBodyCall = retroApi.getMaxSelection(getString(R.string.photographer_id), album.id.toString())
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

        Toast.makeText(this, "Max Selection limit has been changed", Toast.LENGTH_LONG).show()
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
