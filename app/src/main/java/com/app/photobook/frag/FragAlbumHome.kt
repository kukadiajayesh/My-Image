package com.app.photobook.frag

import android.Manifest
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.app.photobook.CustomApp
import com.app.photobook.R
import com.app.photobook.adapter.AlbumAdapter
import com.app.photobook.helper.ImageDownloadAndSave
import com.app.photobook.model.Album
import com.app.photobook.model.AlbumImage
import com.app.photobook.model.AlbumRes
import com.app.photobook.model.User
import com.app.photobook.retro.RetroApi
import com.app.photobook.room.RoomDatabaseClass
import com.app.photobook.tools.Constants
import com.app.photobook.tools.FileUtils
import com.app.photobook.tools.MyPrefManager
import com.app.photobook.tools.Utils
import com.app.photobook.ui.MainActivity
import com.wooplr.spotlight.SpotlightView
import kotlinx.android.synthetic.main.frag_album.view.*
import kotlinx.android.synthetic.main.navigation_toolbar.view.*
import kotlinx.android.synthetic.main.view_empty.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*

class FragAlbumHome : Fragment() {

    internal lateinit var albumAdapter: AlbumAdapter
    internal lateinit var roomDatabaseClass: RoomDatabaseClass
    internal var albums = ArrayList<Album>()
    internal lateinit var progressDialog: ProgressDialog
    internal lateinit var retroApi: RetroApi

    internal lateinit var myPrefManager: MyPrefManager
    internal var user: User? = null

    internal var isDetached = false

    internal lateinit var edtPin: EditText
    internal lateinit var alertDialog: AlertDialog

    internal var downloadedImage = 0
    internal lateinit var album: Album
    internal var pin = ""

    internal var saveArrayList = ArrayList<ImageDownloadAndSave>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.frag_album, container, false)

        myPrefManager = MyPrefManager(activity)
        user = myPrefManager.userDetails

        view!!.tvEmptyMsg.typeface = CustomApp.getFontNormal()

        val app = activity!!.application as CustomApp
        retroApi = app.retroApi

        setBroadcast()

        var title = "Private Gallery"
        if ((activity as MainActivity).photographer != null &&
                !TextUtils.isEmpty((activity as MainActivity).photographer.privateGalleryLabel)) {
            title = (activity as MainActivity).photographer.privateGalleryLabel
        }
        view.toolbar.title = title
        //view.toolbar.subtitle = "Album Gallery"

        view.btnRetry.visibility = View.VISIBLE
        view.btnRetry.setOnClickListener(onClickListener)
        view.llAdd.setOnClickListener(onClickListener)

        progressDialog = ProgressDialog(activity)
        progressDialog.setTitle("")
        progressDialog.setMessage("Please Wait...")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setCancelable(true)
        progressDialog.setOnCancelListener {

            if (downloadedImage != album.images.size) {

                val current = downloadedImage - 1
                for (i in current until saveArrayList.size) {
                    saveArrayList[i].cancel(true)
                }
                removeLocalAlbumFiles(album)
            }
        }

        roomDatabaseClass = CustomApp.getRoomDatabaseClass()

        DatabaseSync().execute()

        view.postDelayed({
            showHelp()
        }, 1000)

        return view
    }


    fun showHelp() {

        SpotlightView.Builder(activity)
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
                .target(view!!.ivAdd)
                .lineAnimDuration(400)
                .lineAndArcColor(Color.parseColor("#eb273f"))
                .dismissOnTouch(true)
                .dismissOnBackPress(true)
                .enableDismissAfterShown(true)
                .usageId("2") //UNIQUE ID
                .show()
    }

    internal var downloadListener: ImageDownloadAndSave.DownloadListener = object : ImageDownloadAndSave.DownloadListener {
        override fun onComplete(pos: Int, filename: String) {

            var image = album.images[pos]

            image.localFilePath = filename

            //Set Image width and height
            val size = Utils.getBitmapWidthHeight(filename)
            image.width = size[0]
            image.height = size[1]

            if (downloadedImage == album.images.size) {
                progressDialog.dismiss()
                insertAlbumInDb()
            }

            downloadedImage++
            progressDialog.setMessage("Downloading...(" +
                    downloadedImage + "/" + album.images.size + ")")

        }

        override fun onError() {
            progressDialog.dismiss()
            Toast.makeText(activity, "Download Interrupted, Try again !", Toast.LENGTH_LONG).show()
        }
    }


    internal var responseBodyCallback: Callback<AlbumRes> = object : Callback<AlbumRes> {
        override fun onResponse(call: Call<AlbumRes>, response: Response<AlbumRes>) {

            if (isDetached) {
                return
            }

            try {

                var albumRes = response.body()

                if (albumRes.error == 0) {
                    alertDialog.dismiss()
                    this@FragAlbumHome.album = albumRes.album
                    album.localPath = FileUtils.getDefaultFolder(activity) + album.id + "/"
                    startAlbumFetchProcess()
                } else {
                    Utils.showDialog(activity, albumRes.message, null)
                    progressDialog.dismiss()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                progressDialog.dismiss()
            }

        }

        override fun onFailure(call: Call<AlbumRes>, t: Throwable) {

            if (isDetached) {
                return
            }

            progressDialog.dismiss()
        }
    }

    internal var onClickListener: View.OnClickListener = View.OnClickListener { view ->
        when {
            view.id == R.id.btnSubmit -> {

                pin = edtPin.text.toString().trim { it <= ' ' }
                if (pin.isEmpty()) {
                    Toast.makeText(activity, "Enter Pin code", Toast.LENGTH_SHORT).show()
                    return@OnClickListener
                }

                Utils.hidekeyboard(activity, edtPin)
                //alertDialog.dismiss()

                fetchAlbum()
            }
            view.id == R.id.btnCancel -> {
                edtPin.setText("")
                alertDialog.dismiss()
            }
            view.id == R.id.btnCancel -> getAlbum()
            view.id == R.id.llAdd -> showDialog()
        }
    }

    internal inner class DatabaseSync : AsyncTask<Void, Void, ArrayList<Album>>() {

        override fun doInBackground(vararg voids: Void): ArrayList<Album> {

            val allAlbums = roomDatabaseClass.daoAlbum()
                    .allAlbums as ArrayList<Album>

            for (album in allAlbums) {
                //album.pbName = album.pbName;
                album.images = roomDatabaseClass.daoAlbumImage().getAllAlbums(album.id) as ArrayList<AlbumImage>
            }

            return allAlbums
        }

        override fun onPostExecute(album: ArrayList<Album>) {
            super.onPostExecute(album)

            if (isDetached) {
                return
            }

            this@FragAlbumHome.albums = album
            view!!.recyclerView!!.layoutManager = LinearLayoutManager(activity)
            albumAdapter = AlbumAdapter(activity!!, this@FragAlbumHome, album)
            view!!.recyclerView!!.adapter = albumAdapter
        }
    }

    internal fun fetchAlbum() {

        if (GetAllPermission()) {
            getAlbum()
        }
    }

    fun refresh(album: Album) {

        if (!Utils.isOnline(activity)) {
            Utils.showNoInternetMessage(activity, view!!.btnRetry)
            return
        }

        this.pin = album.eventPassword

        //Remove local files
        removeLocalAlbumFiles(album)

        //Remove local db
        roomDatabaseClass.daoAlbumImage().DeleteAll(album.images)
        roomDatabaseClass.daoAlbum().Delete(album)

        //Fetch current album details
        getAlbum()
    }

    private fun removeLocalAlbumFiles(album: Album) {
        val sdCard = FileUtils.getDefaultFolder(activity) + album.id
        val myDir = File(sdCard)
        Log.e(TAG, "path: $myDir")
        if (myDir.exists()) {
            FileUtils.deleteRecursive(myDir)
        }
    }

    private fun getAlbum() {

        if (!Utils.isOnline(activity)) {
            Utils.showNoInternetMessage(activity, view!!.btnRetry)
            progressDialog.dismiss()
            return
        }

        progressDialog.setMessage("Please wait...")
        progressDialog.show()

        val responseBodyCall = retroApi.getAlbum(pin, user!!.id.toString())
        responseBodyCall.enqueue(responseBodyCallback)
    }

    private fun startAlbumFetchProcess() {

        val albumss = roomDatabaseClass.daoAlbum().getAlbumId(album.id)
        if (albumss.size == 0) {
            startDownloadImages()
        } else {
            progressDialog.dismiss()
            Toast.makeText(activity, "Album already added !", Toast.LENGTH_LONG).show()
        }
    }

    private fun startDownloadImages() {

        //Downloading Images
        downloadedImage = 1
        progressDialog.setMessage("Downloading...(" +
                downloadedImage + "/" + album.images.size + ")")

        for ((pos, albumImage) in album.images.withIndex()) {

            val imageDownloadAndSave = ImageDownloadAndSave(activity,
                    downloadListener)

            imageDownloadAndSave.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
                    albumImage.url, pos.toString() + "", album.id.toString() + "",
                    (pos + 1).toString() + ".jpg")

            /*if (pos == 1) {
                imageDownloadAndSave.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
                        albumImage.url, (pos - 1).toString() + "", album.id.toString() + "", "front-cover.jpg")
            } else {
                imageDownloadAndSave.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
                        albumImage.url, (pos - 1).toString() + "", album.id.toString() + "", pos.toString() + ".jpg")
            }*/

            saveArrayList.add(imageDownloadAndSave)
        }
    }

    /**
     * Add Album into local database
     *
     * @return boolean
     */
    private fun insertAlbumInDb() {
        //Insert album into database
        roomDatabaseClass.daoAlbum().insert(album)

        for (albumImage in album.images) {
            albumImage.albumId = album.id
        }
        roomDatabaseClass.daoAlbumImage().insertAll(album.images)

        DatabaseSync().execute()

        //getPhotographerInfo(album.id.toString());

        //Add item in list
        //albums.add(album);
        //albumAdapter.notifyDataSetChanged();
        //ShowEmptyMessage(false, false, null, null);
    }

    internal fun ShowEmptyMessage(value: Boolean, showRetry: Boolean, msg: String, img: Drawable) {

        if (value) {
            view!!.frmEmpty!!.visibility = View.VISIBLE
            view!!.tvEmptyMsg!!.text = msg
            view!!.recyclerView!!.visibility = View.GONE
            view!!.ivWifi!!.setImageDrawable(img)
            if (showRetry)
                view!!.btnRetry!!.visibility = View.VISIBLE
            else
                view!!.btnRetry!!.visibility = View.GONE

        } else {
            view!!.recyclerView!!.visibility = View.VISIBLE
            view!!.frmEmpty!!.visibility = View.GONE
        }
    }

    override fun onDetach() {
        super.onDetach()
        isDetached = true
    }

    /*@OnClick(R.id.llAdd)
    fun onViewClicked() {
        showDialog()
    }*/

    internal fun showDialog() {
        val viewDialog = LayoutInflater.from(activity)
                .inflate(R.layout.dialog_add_album, null)
        edtPin = viewDialog.findViewById(R.id.edtPincode)

        viewDialog.findViewById<View>(R.id.btnSubmit).setOnClickListener(onClickListener)
        viewDialog.findViewById<View>(R.id.btnCancel).setOnClickListener(onClickListener)

        val builder = AlertDialog.Builder(activity!!)
        builder.setView(viewDialog)
        builder.setTitle("Add Album")
        alertDialog = builder.create()
        alertDialog.show()
    }


    internal fun GetAllPermission(): Boolean {

        val writeStorage = ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readStorage = ContextCompat.checkSelfPermission(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE)

        val permiss = ArrayList<String>()

        if (writeStorage != PackageManager.PERMISSION_GRANTED) {
            permiss.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (readStorage != PackageManager.PERMISSION_GRANTED) {
            permiss.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (permiss.size > 0) {
            //val per = arrayOfNulls<String>(permiss.size)
            requestPermissions(permiss.toTypedArray(), REQUEST_CODE_PERMISSION)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {

            REQUEST_CODE_PERMISSION -> {

                var isGrandAll = true
                for (i in grantResults.indices) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        isGrandAll = false
                        break
                    }
                }

                if (!isGrandAll) {
                    Toast.makeText(activity, "you need to grant all permission ", Toast.LENGTH_LONG).show()
                    return
                } else {
                    getAlbum()
                }
            }
        }
    }

    fun setBroadcast() {
        var intentFilter = IntentFilter(Constants.ACTION_UPDATE_ALBUMS)
        LocalBroadcastManager.getInstance(context!!)
                .registerReceiver(broadcastReceiver, intentFilter)
    }

    var broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            DatabaseSync().execute()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(context!!)
                .unregisterReceiver(broadcastReceiver)
    }

    companion object {

        private val TAG = FragAlbumHome::class.java.simpleName
        internal val REQUEST_CODE_PERMISSION = 1002
    }

    /*    void getPhotographerInfo(final String albumId) {

        Call<PhotographerRes> responseBodyCall = retroApi.getPhotgrapherDetail(albumId);
        responseBodyCall.enqueue(new Callback<PhotographerRes>() {
            @Override
            public void onResponse(Call<PhotographerRes> call, Response<PhotographerRes> response) {

                PhotographerRes photographerRes = response.body();
                if (!photographerRes.error) {

                    //Insert Photographer
                    photographerRes.data.pb_id = albumId;
                    roomDatabaseClass.daoPhotographer().insert(photographerRes.data);
                    //==//

                    if (!TextUtils.isEmpty(photographerRes.data.logoUrl)) {
                        Picasso.with(getActivity())
                                .load(photographerRes.data.logoUrl)
                                .fetch();
                    }
                }
            }

            @Override
            public void onFailure(Call<PhotographerRes> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }*/
}
