package com.app.photobook.ui

import android.net.Uri
import android.support.v4.view.ViewPager
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.alexvasilkov.gestures.Settings
import com.alexvasilkov.gestures.commons.RecyclePagerAdapter
import com.alexvasilkov.gestures.views.GestureImageView
import com.app.photobook.model.AlbumImage
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import java.io.File


class ViewPagerAdapter(var viewPager: ViewPager, var albumImages: List<AlbumImage>, var liveMode: Boolean)
    : RecyclePagerAdapter<ViewPagerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(container: ViewGroup): ViewHolder {
        var holder = ViewHolder(container)

        //holder.image.scaleType = ImageView.ScaleType.FIT_CENTER\
        apply(holder.image)

        // Enabling smooth scrolling when image panning turns into ViewPager scrolling.
        // Otherwise ViewPager scrolling will only be possible when image is in zoomed out state.
        holder.image.controller.enableScrollInViewPager(viewPager)

        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // Applying settings from toolbar menu, see BaseExampleActivity
        //apply(holder.image)

        if (liveMode) {

            val options = RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(Target.SIZE_ORIGINAL)
                    .dontTransform()

            Glide.with(holder.image)
                    .load(albumImages[position].url)
                    .apply(options)
                    //.thumbnail(thumbRequest)
                    .into(holder.image)

        } else {

            var path = albumImages[position].localFilePath
            //holder.image.setImageURI(Uri.fromFile(File(path)))

            val options = RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(Target.SIZE_ORIGINAL)
                    .dontTransform()

            Glide.with(holder.image)
                    .load(Uri.fromFile(File(path)))
                    .apply(options)
                    //.thumbnail(thumbRequest)
                    .into(holder.image)
        }


        /*val painting = paintings[position]
        GlideHelper.loadFull(holder.image, painting.imageId, painting.thumbId)*/
    }

    fun apply(imageView: GestureImageView) {
        imageView.controller
                .settings
                .setPanEnabled(true)
                .setZoomEnabled(true)
                .setRotationEnabled(false)
                .setRestrictRotation(false)
                .setFillViewport(true)
                .setFitMethod(Settings.Fit.INSIDE)
                .setBoundsType(Settings.Bounds.NORMAL)
                .setGravity(Gravity.CENTER)
                .setAnimationsDuration(Settings.ANIMATIONS_DURATION)
                .setMaxZoom(8f)
                .setOverscrollDistance((imageView as View).context, 32f, Settings.OVERZOOM_FACTOR)
                .doubleTapZoom = 8f


        /*imageView.controller
                .settings
                .setPanEnabled(true)
                .setZoomEnabled(true)
                .setDoubleTapEnabled(false)
                .setRotationEnabled(false)
                .setRestrictRotation(false)
                .setOverscrollDistance((imageView as View).context, 32f, Settings.OVERZOOM_FACTOR)
                .setOverzoomFactor(Settings.OVERZOOM_FACTOR)
                .setFillViewport(true)
                .setFitMethod(Settings.Fit.INSIDE)
                .setExitEnabled(true)
                .setBoundsType(Settings.Bounds.NORMAL)
                .setGravity(Gravity.CENTER)
                .setMaxZoom(6f)
                .setAnimationsDuration(Settings.ANIMATIONS_DURATION)*/
    }

    override fun getCount(): Int {
        return albumImages.size
    }

    class ViewHolder(container: ViewGroup) :
            RecyclePagerAdapter.ViewHolder(GestureImageView(container.context)) {
        val image: GestureImageView = itemView as GestureImageView
    }
}