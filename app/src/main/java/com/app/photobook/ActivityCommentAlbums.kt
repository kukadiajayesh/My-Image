package com.app.photobook

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.app.photobook.adapter.AlbumCommentAdapter
import com.app.photobook.model.AlbumImage
import com.app.photobook.model.AlbumRes
import com.app.photobook.retro.RetroApi
import com.app.photobook.tools.Utils
import kotlinx.android.synthetic.main.activity_album_comments.*
import kotlinx.android.synthetic.main.navigation_toolbar.*
import kotlinx.android.synthetic.main.view_empty.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ActivityCommentAlbums : BaseActivity() {

    internal var pbId: String? = null
    internal var albumImageBckArrayList = ArrayList<AlbumImage>()
    internal lateinit var albumCommentAdapter: AlbumCommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_album_comments)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        title = "Comments"

        if (intent.hasExtra("pb_id")) {
            pbId = intent.extras!!.getString("pb_id")
        }

        albumCommentAdapter = AlbumCommentAdapter(this, albumImageBckArrayList)
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.adapter = albumCommentAdapter

        fetchAlbumComments(pbId)
    }


    internal var responseBodyCallback: Callback<AlbumRes> = object : Callback<AlbumRes> {
        override fun onResponse(call: Call<AlbumRes>, response: Response<AlbumRes>) {

            try {

                var albumRes = response.body()

                if (albumRes.error == 0) {

                    var album = albumRes.album

                    for (albumImageBck in album.images) {
                        //albumImageBck.id = albumImageBck.pageId
                        albumImageBck.albumId = album.id
                    }

                    albumImageBckArrayList.addAll(album.images)
                    albumCommentAdapter.notifyDataSetChanged()
                    recyclerView!!.visibility = View.VISIBLE

                    ShowEmptyMessage(false, false,
                            getString(R.string.message_empty_comment),
                            ContextCompat.getDrawable(this@ActivityCommentAlbums,
                                    R.drawable.ic_svg_data_no))

                } else {
                    ShowEmptyMessage(true, false,
                            getString(R.string.message_empty_comment),
                            ContextCompat.getDrawable(this@ActivityCommentAlbums,
                                    R.drawable.ic_svg_data_no))
                }

            } catch (e: Exception) {
                e.printStackTrace()
                ShowEmptyMessage(true, false,
                        getString(R.string.message_empty_comment),
                        ContextCompat.getDrawable(this@ActivityCommentAlbums,
                                R.drawable.ic_svg_data_no))
            }

        }

        override fun onFailure(call: Call<AlbumRes>, t: Throwable) {

            ShowEmptyMessage(true, false,
                    getString(R.string.message_empty_comment),
                    ContextCompat.getDrawable(this@ActivityCommentAlbums,
                            R.drawable.ic_svg_data_no))
        }
    }

    internal fun fetchAlbumComments(pdId: String?) {

        if (!Utils.isOnline(this)) {
            Utils.showNoInternetMessage(this, btnRetry)
            ShowEmptyMessage(true, true, getString(R.string.message_no_internet),
                    ContextCompat.getDrawable(this, R.drawable.ic_svg_wifi))
            return
        }

        recyclerView!!.visibility = View.GONE
        rlProgress!!.visibility = View.VISIBLE

        var responseBodyCall = retroApi.getAlbumComments(getString(R.string.photographer_id), pdId)
        responseBodyCall.enqueue(responseBodyCallback)
    }

    internal fun ShowEmptyMessage(value: Boolean, showRetry: Boolean, msg: String, img: Drawable?) {

        if (value) {
            rlProgress!!.visibility = View.GONE
            frmEmpty!!.visibility = View.VISIBLE
            tvEmptyMsg!!.text = msg
            recyclerView!!.visibility = View.GONE
            ivWifi!!.setImageDrawable(img)
            if (showRetry)
                btnRetry!!.visibility = View.VISIBLE
            else
                btnRetry!!.visibility = View.GONE

        } else {
            rlProgress!!.visibility = View.GONE
            recyclerView!!.visibility = View.VISIBLE
            frmEmpty!!.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun getRetroAPI(): RetroApi {
        return retroApi
    }
}
