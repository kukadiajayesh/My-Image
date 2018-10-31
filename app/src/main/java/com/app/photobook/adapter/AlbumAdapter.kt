package com.app.photobook.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.photobook.R
import com.app.photobook.frag.FragAlbumHome
import com.app.photobook.model.Album
import com.app.photobook.tools.BitmapUtils
import com.app.photobook.tools.Constants
import com.app.photobook.tools.FileUtils
import com.app.photobook.tools.SharingUtils
import com.app.photobook.ui.ActivityAlbumViewWebview
import com.app.photobook.ui.ActivityCommentAlbums
import com.app.photobook.ui.StaggeredGalleryActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.list_item_photo_album.view.*
import java.io.File
import java.util.*

/**
 * Created by Jayesh on 9/26/2017.
 */

class AlbumAdapter(internal var context: Context, internal var fragAlbumHome: FragAlbumHome,
                   internal var albums: ArrayList<Album>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_photo_album, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val album = albums[position]
        holder.itemView.tvTitle!!.text = album.eventName + Constants.BLANK_SPACES

        //viewHolder.tvPhotograph.setText(album.photographyBy);
        /*try {
            viewHolder.tvDate.setText(DateFormat.format("dd-MM-yyyy",
                    Utils.convertTime(album.date, "yyyy-MM-dd")));
        } catch (Exception e) {
            e.printStackTrace();
            viewHolder.tvDate.setText(album.date);
        }*/

        if (album.images != null) {
            if (album.images.size > 0) {
                Glide.with(context)
                        .load(album.images[0].url)
                        .into(holder.itemView.ivImage!!)
            }
        }

        if (album.eventType == Constants.GALLERY_TYPE_ALBUM) {
            holder.itemView.llAutoPlay.visibility = View.VISIBLE
        } else {
            holder.itemView.llAutoPlay.visibility = View.GONE

            if (album.eventType == Constants.GALLERY_TYPE_SELECTION) {
                holder.itemView.llShare.visibility = View.GONE
            }
        }

        if (album.isSharble == 1) {
            holder.itemView.llShare.visibility = View.VISIBLE
        } else {
            holder.itemView.llShare.visibility = View.GONE
        }

        val customListener = CustomListener(album, position)
        holder.itemView.llItem!!.setOnClickListener(customListener)
        holder.itemView.llAutoPlay!!.setOnClickListener(customListener)
        holder.itemView.llComment!!.setOnClickListener(customListener)
        holder.itemView.llShare!!.setOnClickListener(customListener)
        holder.itemView.llRefresh!!.setOnClickListener(customListener)
        holder.itemView.ivDelete!!.setOnClickListener(customListener)
    }

    internal inner class CustomListener(var album: Album, var pos: Int) : View.OnClickListener {

        override fun onClick(view: View) {

            when (view.id) {
                R.id.ivDelete -> {
                    fragAlbumHome.onDelete(album, pos)
                }
                R.id.llItem -> {

                    if (album.eventType == Constants.GALLERY_TYPE_ALBUM) {

                        val intent = Intent(context, ActivityAlbumViewWebview::class.java)
                        intent.putExtra("album", album)
                        context.startActivity(intent)

                    } else {
                        val intent = Intent(context, StaggeredGalleryActivity::class.java)
                        intent.putExtra("album", album)
                        context.startActivity(intent)
                    }

                    /*//Intent intent = new Intent(context, ActivityAlbumViewWebview.class);
                    val intent = Intent(context, StaggeredGalleryActivity::class.java)
                    //intent.putExtra("images", albums.get(pos).images);
                    intent.putExtra("album", album)
                    context.startActivity(intent)*/
                }
                R.id.llAutoPlay -> {

                    val intent = Intent(context, ActivityAlbumViewWebview::class.java)
                    intent.putExtra("album", album)
                    intent.putExtra("auto_play", true)
                    context.startActivity(intent)

                    /*Intent intent = new Intent(context, ActivityAlbumView_.class);
                    intent.putExtra("album", album);
                    intent.putExtra("auto_play", true);
                    context.startActivity(intent);
                    */
                }
                R.id.llComment -> {

                    val pdId = album.id.toString() + ""
                    val intent = Intent(context, ActivityCommentAlbums::class.java)
                    intent.putExtra("pb_id", pdId)
                    context.startActivity(intent)

                }
                R.id.llShare -> {

                    /*String text = "Hello Friends, View my Photobook " + albums.get(pos).pbName + "\n" +
                            "Download the app from: "
                            + context.getPackageName() + " & use Pin : " + albums.get(pos).pbPassword;*/

                    /*val text = String.format(context.getString(R.string.share_text), album.eventName,
                            album.eventPassword)
                    val uri = Uri.parse(album.images[0].localFilePath)
                    SharingUtils.shareAlbum(context, text, uri)*/

                    val text = album.shareMessage
                    if (album.isOffline == 1) {
                        SharingUtils.shareAlbum(context, text, File(album.images[0].localFilePath))
                    } else {
                        Glide.with(context)
                                .asBitmap()
                                .load(album.images[0].url)
                                .into(object : SimpleTarget<Bitmap>() {
                                    override fun onResourceReady(resource: Bitmap?, transition: Transition<in Bitmap>?) {
                                        var file = BitmapUtils.getBitmapFromDrawable(context, resource)
                                        try {
                                            SharingUtils.shareAlbum(context, text, file)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                })
                    }
                }

                R.id.llRefresh -> {
                    FileUtils.deleteCache(context)
                    fragAlbumHome.refresh(album)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return albums.size
    }

    internal class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
