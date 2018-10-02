package com.app.photobook.adapter.portfolio

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.photobook.R
import com.app.photobook.WebViewActivity
import com.app.photobook.YoutubeActivity
import com.app.photobook.model.Video
import com.app.photobook.tools.Constants
import com.app.photobook.tools.Constants.VIMEO_THUMB_VIEW
import com.app.photobook.tools.Constants.YOUTUBE_THUMB_VIEW
import com.app.photobook.tools.Utils
import com.app.photobook.tools.Utils.getContentFromUrl
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_item_photo_video_portfolio.view.*
import org.json.JSONArray
import java.util.*

/**
 * Created by Jayesh on 9/26/2017.
 */

class VideoAdapter(internal var context: Context,
                   internal var albums: ArrayList<Video>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_photo_video_portfolio, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val album = albums[position]
        holder.itemView.tvTitle!!.text = album.videoTitle
        holder.itemView.tvDesc!!.text = album.videoDesc

        if (album.iframeUrl != null) {

            var url = ""
            album.youtubeId = Utils.getLastPartFromUrl(album.iframeUrl)

            if (album.videoType == Constants.VIDEO_TYPE_YOUTUBE) {

                url = YOUTUBE_THUMB_VIEW.replace("#ID", album.youtubeId)
                holder.itemView.ivPlayThumb.setImageResource(R.drawable.ic_svg_youtube_)
                holder.itemView.ivPlayThumb.setBackground(null)

                if (!TextUtils.isEmpty(url)) {
                    Glide.with(context)
                            .load(url)
                            .into(holder.itemView.ivImage!!)

                }

            } else if (album.videoType == Constants.VIDEO_TYPE_VIMEO) {

                url = VIMEO_THUMB_VIEW.replace("#ID", album.youtubeId)
                holder.itemView.ivPlayThumb.setImageResource(R.drawable.ic_svg_video_play_)
                holder.itemView.ivPlayThumb.setBackgroundResource(R.drawable.shape_backgrond_rounded)

                if (!TextUtils.isEmpty(url)) {
                    var thumbTask = ThumbTask(url, holder)
                    thumbTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR)

                }
            }
        }

        val customListener = CustomListener(position, holder)
        holder.itemView.llItem!!.setOnClickListener(customListener)
    }

    internal inner class ThumbTask(var url: String, var viewHolder: RecyclerView.ViewHolder)
        : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String {
            return getContentFromUrl(url)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            if (!TextUtils.isEmpty(result)) {

                try {
                    var jsonArray = JSONArray(result)
                    var jsonObject = jsonArray.getJSONObject(0)
                    var thumbs = jsonObject.getString("thumbnail_large")

                    if (!TextUtils.isEmpty(thumbs)) {
                        Glide.with(context)
                                .load(thumbs)
                                .into(viewHolder.itemView.ivImage!!)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    internal inner class CustomListener(var pos: Int, var viewHolder: RecyclerView.ViewHolder) : View.OnClickListener {

        override fun onClick(view: View) {
            val video = albums[pos]

            when {
                view.id == R.id.ivDelete -> //fragClientFormList.delete(myPointArrayList.get(pos).reward_id, pos);
                    albums.removeAt(pos)
                view.id == R.id.llItem -> {

                    if (video.videoType == Constants.VIDEO_TYPE_YOUTUBE) {

                        val intent = Intent(context, YoutubeActivity::class.java)
                        intent.putExtra("url", video.youtubeId)
                        context.startActivity(intent)

                    } else {

                        val intent = Intent(context, WebViewActivity::class.java)
                        var myUrl = Constants.VIDEO_PLAY_IFRAME
                                //.replace("#width", height.toString())
                                //.replace("#height", width.toString())
                                .replace("#src", video.iframeUrl + video.queryString)

                        intent.putExtra("url", video.iframeUrl + video.queryString)
                        //intent.putExtra("url", video.videoUrl)
                        //intent.putExtra("url", myUrl)
                        context.startActivity(intent)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return albums.size
    }

    internal class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    companion object {
        private var TAG = VideoAdapter.javaClass.simpleName
    }

}
