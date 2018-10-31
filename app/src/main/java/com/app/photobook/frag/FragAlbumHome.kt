package com.app.photobook.frag

import android.Manifest
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
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
import com.app.photobook.model.*
import com.app.photobook.retro.RetroApi
import com.app.photobook.room.RoomDatabaseClass
import com.app.photobook.tools.*
import com.app.photobook.ui.MainActivity
import kotlinx.android.synthetic.main.frag_album.view.*
import kotlinx.android.synthetic.main.navigation_toolbar.view.*
import kotlinx.android.synthetic.main.view_empty.view.*
import org.apache.http.HttpStatus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

            if (downloadedImage != album.images.size && saveArrayList.size > 0) {

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

        if (isDetached) {
            return
        }

        ShowcaseUtils(activity)
                .showInAlbumAddScreen(view!!.ivAdd)
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
                    if (alertDialog != null) {
                        alertDialog.dismiss()
                    }

                    this@FragAlbumHome.album = albumRes.album
                    album.localPath = album.getAlbumPath(activity, true).absolutePath + "/"

                    //1 to offline gallery
                    if (album.isOffline == 1) {
                        offlinePrompt()
                    } else {
                        downloadAlbum(false)
                    }

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

    fun offlinePrompt() {
        AlertDialog.Builder(activity)
                .setTitle("")
                .setMessage(getString(R.string.dialog_msg_offline))
                .setPositiveButton("Yes") { dialog, which ->
                    downloadAlbum(true)
                }
                .setNegativeButton("No") { dialog, which ->
                    downloadAlbum(false)
                }
                .show()
    }

    fun downloadAlbum(isOffline: Boolean) {
        startAlbumFetchProcess(isOffline)
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

            checkAlbumsIsActive()
        }
    }

    fun onDelete(album: Album, pos: Int) {

        AlertDialog.Builder(context!!)
                .setMessage("Do you want to delete this gallery?")
                .setPositiveButton("Yes") { dialog, which ->

                    //Remove offline folder
                    deleteAlbumAtPos(pos, album)
                }
                .setNegativeButton("No", null)
                .show()
    }

    private fun deleteAlbumAtPos(pos: Int, album: Album?) {

        var myAlbum = album
        if (album == null) {
            myAlbum = albums[pos]
        }

        var albumPath = myAlbum!!.getAlbumPath(activity, false)
        if (albumPath.exists()) {
            var sucess = albumPath.deleteRecursively()
            Log.e(TAG, "Delete album folder: " + sucess.toString())
        }

        //Delete All albums with images
        roomDatabaseClass.daoAlbumImage().DeleteAll(myAlbum.images)
        roomDatabaseClass.daoAlbum().Delete(myAlbum)

        albums.remove(myAlbum)
        albumAdapter.notifyItemRemoved(pos)
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
        var myDir = album.getAlbumPath(activity, false)
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

    private fun startAlbumFetchProcess(isOffline: Boolean) {

        this@FragAlbumHome.album.isOffline = if (isOffline) 1 else 2
        val albumss = roomDatabaseClass.daoAlbum().getAlbumId(album.id)
        if (albumss.size == 0) {
            if (isOffline)
                startDownloadImages()
            else {
                progressDialog.dismiss()
                insertAlbumInDb()
            }

        } else {
            progressDialog.dismiss()
            Toast.makeText(activity, "This Album is already added", Toast.LENGTH_LONG).show()
        }
    }

    private fun startDownloadImages() {

        //Downloading Images
        downloadedImage = 1
        progressDialog.setMessage("Downloading...(" +
                downloadedImage + "/" + album.images.size + ")")

        for ((pos, albumImage) in album.images.withIndex()) {

            val imageDownloadAndSave = ImageDownloadAndSave(activity, album.localPath, downloadListener)

            imageDownloadAndSave.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,
                    albumImage.url, pos.toString() + "", (pos + 1).toString() + ".jpg")

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

    /**
     * Check every pins whether every gallery is active or not
     */
    private fun checkAlbumsIsActive() {

        if (!Utils.isOnline(activity)) return
        if (albums.isEmpty()) return

        //Make command separate pins
        var pins = ""
        albums.forEach {
            pins += it.eventPassword + ","
        }
        pins = pins.substring(0, pins.length - 1)

        val responseBodyCall = retroApi.checkIsAlbumActive(pins)
        responseBodyCall.enqueue(object : retrofit2.Callback<AlbumActiveRes> {
            override fun onResponse(call: Call<AlbumActiveRes>, response: Response<AlbumActiveRes>) {
                try {
                    if (response.code() == HttpStatus.SC_OK) {

                        var res = response.body()
                        if (res.error == 0) {

                            var posDelete = ArrayList<Int>()

                            //get inactive album's pos
                            res.data.forEach { item ->
                                for ((index, value) in albums.withIndex()) {
                                    if (value.id == item.id) {
                                        if (item.isActive == 0) {
                                            posDelete.add(index)
                                        }
                                    }
                                }
                            }


                            if (posDelete.isNotEmpty()) {

                                //posDelete.sort
                                //Collections.sort(posDelete, Collections.reverseOrder())
                                posDelete.sortDescending()

                                //var albumsToDeleteMsg = ""
                                if (posDelete.isNotEmpty()) {

                                    //for (i in posDelete.size - 1 downTo 0) {
                                    for (i in posDelete.indices) {
                                        var deletePos = posDelete[i]
                                        //var album = albums[deletePos]
                                        //albumsToDeleteMsg += (i + 1).toString() + ". " + album.eventName + "\n\n"
                                        deleteAlbumAtPos(deletePos, null)
                                    }
                                }
                                //albumsToDeleteMsg = albumsToDeleteMsg.trim()

                                AlertDialog.Builder(activity)
                                        //.setTitle("These galleries are about to delete")
                                        .setMessage(res.message)
                                        .setPositiveButton("Ok", null)
                                        .show()
                            }
                        } else {
                            Toast.makeText(activity, res.message, Toast.LENGTH_LONG).show()
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

            override fun onFailure(call: Call<AlbumActiveRes>?, t: Throwable?) {
                t!!.printStackTrace()
            }
        }
        )

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
        builder.setTitle(getString(R.string.dialog_title_add_gallery))
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
