package com.app.photobook.adapter.portfolio

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.photobook.ActivityAlbumViewWebview
import com.app.photobook.R
import com.app.photobook.model.Album
import com.app.photobook.tools.Constants
import com.app.photobook.ui.StaggeredGalleryActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_item_photo_album_portfolio.view.*
import java.util.*

/**
 * Created by Jayesh on 9/26/2017.
 */

class AlbumLiveAdapter(internal var context: Context,
                       internal var albums: ArrayList<Album>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_photo_album_portfolio, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val album = albums[position]
        holder.itemView.tvTitle!!.text = "Title: " + album.eventName + Constants.BLANK_SPACES

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
        }

        val customListener = CustomListener(position, holder)
        holder.itemView.llItem!!.setOnClickListener(customListener)
        holder.itemView.llAutoPlay!!.setOnClickListener(customListener)
    }

    internal inner class CustomListener(var pos: Int, var viewHolder: RecyclerView.ViewHolder) : View.OnClickListener {

        override fun onClick(view: View) {
            val album = albums[pos]

            when {
                view.id == R.id.ivDelete -> //fragClientFormList.delete(myPointArrayList.get(pos).reward_id, pos);
                    albums.removeAt(pos)
                view.id == R.id.llItem -> {

                    if (album.eventType == Constants.GALLERY_TYPE_ALBUM) {

                        val intent = Intent(context, ActivityAlbumViewWebview::class.java)
                        intent.putExtra("album", album)
                        intent.putExtra("live_mode", true)
                        context.startActivity(intent)

                    } else {
                        val intent = Intent(context, StaggeredGalleryActivity::class.java)
                        intent.putExtra("album", album)
                        intent.putExtra("live_mode", true)
                        context.startActivity(intent)
                    }
                }
                view.id == R.id.llAutoPlay -> {

                    val intent = Intent(context, ActivityAlbumViewWebview::class.java)
                    intent.putExtra("album", album)
                    intent.putExtra("auto_play", true)
                    intent.putExtra("live_mode", true)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return albums.size
    }

    internal class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
