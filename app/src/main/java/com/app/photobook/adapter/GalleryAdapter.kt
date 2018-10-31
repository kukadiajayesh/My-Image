package com.app.photobook.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.photobook.CustomApp
import com.app.photobook.R
import com.app.photobook.model.Album
import com.app.photobook.model.AlbumImage
import com.app.photobook.tools.Constants
import com.app.photobook.tools.SharingUtils
import com.app.photobook.ui.PhotoPreviewActivity
import com.app.photobook.ui.StaggeredGalleryActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.squareup.picasso.Callback
import kotlinx.android.synthetic.main.view_album_image.view.*
import java.io.File
import java.util.*

/**
 * Created by Jayesh on 11/22/2017.
 */

class GalleryAdapter(private val staggeredGalleryActivity: StaggeredGalleryActivity,
                     var album: Album,
                     var albumImages: ArrayList<AlbumImage>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    internal var roomDatabaseClass = CustomApp.getRoomDatabaseClass()
    var handler = Handler()

    private val set = ConstraintSet()
    var mHeighMin: Int = 0
    var mHeightMax: Int = 0

    init {
        mHeighMin = staggeredGalleryActivity.getResources().getDimension(R.dimen.height_min).toInt()
        mHeightMax = staggeredGalleryActivity.getResources().getDimension(R.dimen.height_max).toInt()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val li = staggeredGalleryActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = li.inflate(R.layout.view_album_image,
                parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val albumImage = albumImages!![position]

        if (albumImage.holderHeight == 0) {
            albumImage.holderHeight = getRandomIntInRange(mHeightMax, mHeighMin)
            //holder.itemView.layoutParams.height = albumImage.holderHeight
        }
        holder.itemView.layoutParams.height = albumImage.holderHeight

        if (album.eventType == Constants.GALLERY_TYPE_SELECTION
                && !staggeredGalleryActivity.liveMode) {
            holder.itemView.chk.visibility = View.VISIBLE
            holder.itemView.viewShadow.visibility = View.VISIBLE
        } else {
            holder.itemView.chk.visibility = View.GONE
            holder.itemView.viewShadow.visibility = View.GONE
        }
        holder.itemView.ivShare.visibility = View.GONE
        holder.itemView.ivComment.visibility = View.GONE

        val customListener = CustomListener(position, holder)
        holder.itemView.setOnClickListener(customListener)
        holder.itemView.chk.setOnClickListener(customListener)
        holder.itemView.ivShare.setOnClickListener(customListener)
        holder.itemView.ivComment.setOnClickListener(customListener)

        if (staggeredGalleryActivity.liveMode) {
            /* Picasso.with(staggeredGalleryActivity)
                     .load(albumImage.url)
                     .into(holder.itemView.album_image)*/

            val options = RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(Target.SIZE_ORIGINAL)
                    .placeholder(R.drawable.shape_image_thumb)
                    .dontTransform()

            Glide.with(holder.itemView.album_image)
                    .load(albumImage.url)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(options)
                    .into(holder.itemView.album_image)

        } else {

            val options = RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(Target.SIZE_ORIGINAL)
                    .placeholder(R.drawable.shape_image_thumb)
                    .dontTransform()

            if (album.isOffline == 1) {
                //updateBorderSize(holder)

                if (!TextUtils.isEmpty(albumImage.localFilePath)) {

                    //Uri uri = Uri.parse(albumImageBck.localFilePath);
                    //viewHolder.album_image.setImageURI(uri);

                    val file = File(albumImage.localFilePath)
                    /*
                       Picasso.with(staggeredGalleryActivity)
                               .load(file)
                               .into(holder.itemView.album_image, customListener)*/

                    //updateBorderSize(holder)
                    Glide.with(staggeredGalleryActivity)
                            .load(Uri.fromFile(file))
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .apply(options)
                            .listener(customListener)
                            .into(holder.itemView.album_image)
                } else {
                    holder.itemView.album_image!!.setImageResource(R.drawable.nophotos)
                }

            } else {
                Glide.with(holder.itemView.album_image)
                        .load(albumImage.url)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .apply(options)
                        .listener(customListener)
                        .into(holder.itemView.album_image)
            }
        }
    }

    internal inner class CustomListener(var pos: Int, var holder: RecyclerView.ViewHolder) :
            View.OnClickListener, Callback, RequestListener<Drawable> {

        override fun onClick(view: View) {

            var albumImage = albumImages!![pos]

            when (view.id) {
                R.id.ivShare -> {
                    SharingUtils.shareAlbum(staggeredGalleryActivity, "", File(albumImage.localFilePath))
                }
                R.id.ivComment -> {
                    staggeredGalleryActivity.commentDialog.show(albumImage.albumId, albumImage.pageId)
                }
                R.id.chk -> {

                    if (holder.itemView.chk.isChecked &&
                            !staggeredGalleryActivity.photoSelectionUtils.validateSelection()) {
                        holder.itemView.chk.isChecked = albumImage.selected
                        return
                    }

                    albumImage.selected = !albumImage.selected
                    holder.itemView.chk.isChecked = albumImage.selected
                    setSelectImage(holder, albumImage)

                    //Update selected image into database
                    staggeredGalleryActivity.hasSelectionChanged = true
                    roomDatabaseClass.daoAlbumImage().update(albumImage)

                    //Update visibility of menu
                    staggeredGalleryActivity.showMenu = false
                    for (images in albumImages!!) {
                        if (images.selected) {
                            staggeredGalleryActivity.showMenu = true
                            break
                        }
                    }
                    staggeredGalleryActivity.invalidateOptionsMenu()
                }
                else -> {
                    val mIntent = Intent(staggeredGalleryActivity,
                            PhotoPreviewActivity::class.java)
                    val mBundle = Bundle()
                    mBundle.putParcelableArrayList("albumImages", albumImages)
                    mBundle.putParcelable("album", album)
                    mBundle.putInt("key_pos", pos)
                    mBundle.putBoolean("view_mode", staggeredGalleryActivity.liveMode)
                    mIntent.putExtras(mBundle)
                    staggeredGalleryActivity.startActivity(mIntent)
                }

            }


            /*//PhotoEntry photoEntry = albumImages.get(current);
            AlbumImage_bck photoEntry = albumImages.get(pos);

            UCrop uCrop = UCrop.of(Uri.fromFile(new File(photoEntry.path))
                    , Uri.fromFile(new File(staggeredGalleryActivity.getCacheDir(), "dd.jpg")));

            uCrop = basisConfig(uCrop);
            uCrop = advancedConfig(uCrop);

            uCrop.start(staggeredGalleryActivity);*/

            /*Intent mIntent = new Intent(staggeredGalleryActivity,
                    PhotoPreviewActivity.class);
            Bundle mBundle = new Bundle();
            mBundle.putParcelableArrayList("albumImages", albumImages);
            mBundle.putInt("key_pos", pos);
            mIntent.putExtras(mBundle);
            staggeredGalleryActivity.startActivity(mIntent);*/
        }

        override fun onSuccess() {
            var albumImage = albumImages!![pos]
            setSelectImage(holder, albumImage)
        }

        override fun onError() {
            holder.itemView.parentContsraint.setBackground(null)
            holder.itemView.parentContsraint.setPadding(0, 0, 0, 0)
        }

        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
            return false
        }

        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {

            /*handler.postDelayed({
                albumImages!![pos].imageHeight = holder.itemView.album_image.layoutParams.height
            }, 50)*/

            /* var layoutHeight = resource!!.intrinsicHeight
             Log.e(TAG, "Height: " + albumImages!![pos].height + " = " + layoutHeight)*/
            if (pos <= albumImages!!.size - 1) {
                setSelectImage(holder, albumImages!![pos])
            }
            return false
        }
    }

    fun setSelectImage(holder: RecyclerView.ViewHolder, albumImage: AlbumImage) {

        holder.itemView.chk.isChecked = albumImage.selected
        if (albumImage.selected) {
            holder.itemView.viewBorder.visibility = View.VISIBLE
            //holder.itemView.rlMain.setBackgroundColor(ContextCompat.getColor(staggeredGalleryActivity, R.color.ImageBorderColor))
            //holder.itemView.album_image.setPadding(imageBorder, imageBorder, imageBorder, imageBorder)
            // updateBorderSize(holder)
        } else {
            //holder.itemView.rlMain.setBackground(null)
            //holder.itemView.album_image.setPadding(0, 0, 0, 0)
            holder.itemView.viewBorder.visibility = View.GONE
        }

    }

    fun updateBorderSize(holder: RecyclerView.ViewHolder) {

        //holder.itemView.viewBorder.visibility = View.GONE

        handler.postDelayed({
            holder.itemView.viewBorder.visibility = View.VISIBLE
            var layoutParams = holder.itemView.viewBorder.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.width = holder.itemView.album_image.measuredWidth
            layoutParams.height = holder.itemView.album_image.measuredHeight
            holder.itemView.viewBorder.requestLayout()
            //holder.itemView.viewBorder.animate().alpha(1f).start()
        }, 300)
    }

    override fun getItemCount(): Int {
        return if (albumImages != null) albumImages!!.size else 0
    }

    internal class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private val mRandom = Random()

    // Custom method to get a random number between a range
    protected fun getRandomIntInRange(max: Int, min: Int): Int {
        return mRandom.nextInt(max - min + min) + min
    }

    companion object {
        private var TAG = GalleryAdapter.javaClass.simpleName
    }
}
