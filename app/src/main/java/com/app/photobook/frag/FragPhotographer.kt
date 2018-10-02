package com.app.photobook.frag

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.app.photobook.CustomApp
import com.app.photobook.R
import com.app.photobook.model.Photographer
import com.app.photobook.retro.RetroApi
import com.app.photobook.room.RoomDatabaseClass
import com.app.photobook.tools.Utils
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.frag_photographer.view.*


class FragPhotographer : Fragment() {

    internal lateinit var roomDatabaseClass: RoomDatabaseClass
    internal lateinit var photographer: Photographer

    internal lateinit var retroApi: RetroApi
    internal lateinit var view: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        view = inflater.inflate(R.layout.frag_photographer, container, false)

        roomDatabaseClass = CustomApp.getRoomDatabaseClass()

        loadFromLocal()

        view.llEmail.setOnClickListener(onClick)
        view.llGoogle.setOnClickListener(onClick)
        view.llFacebook.setOnClickListener(onClick)
        view.llInstagram.setOnClickListener(onClick)
        view.llTwitter.setOnClickListener(onClick)
        view.llPintrest.setOnClickListener(onClick)

        return view
    }

    var onClick = View.OnClickListener {
        when (it.id) {
            R.id.llEmail -> {
                Utils.sendEmail(context, photographer.email)
            }
            R.id.llGoogle -> {
                openLink(photographer.googleplusLink)
            }
            R.id.llFacebook -> {
                openLink(photographer.facebookLink)
            }
            R.id.llInstagram -> {
                openLink(photographer.instagramLink)
            }
            R.id.llTwitter -> {
                openLink(photographer.twitterLink)
            }
            R.id.llPintrest -> {
                openLink(photographer.pinterestLink)
            }
        }
    }

    private fun openLink(link: String) {

        var url = link
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://$url"
        }

        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to open this link", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadFromLocal() {

        photographer = roomDatabaseClass.daoPhotographer().clientInfo

        /*if (!TextUtils.isEmpty(photographer.logo)) {
             Picasso.with(this)
                     .load(photographer.logo)
                     .into(ivLogo)
         }*/

        if (!TextUtils.isEmpty(photographer.logo)) {
            Picasso.with(context)
                    .load(photographer.logo)
                    .into(view.ivLogo, object : Callback {
                        override fun onSuccess() {
                        }

                        override fun onError() {
                            view.ivLogo.setImageResource(R.drawable.ic_svg_user_thumb_128dp)
                        }
                    })
        } else {
            view.ivLogo.setImageResource(R.drawable.ic_svg_user_thumb_128dp)
        }

        view.tvName.text = photographer.businessName
        view.tvDesc.text = photographer.biography
        view.tvEmail.text = photographer.email

        if (!TextUtils.isEmpty(photographer.googleplusLink)) {
            view.tvSocialGoogle.text = removeHttp(photographer.googleplusLink)
        } else {
            view.tvSocialGoogle.text = "(not available)"
        }

        if (!TextUtils.isEmpty(photographer.facebookLink)) {
            view.tvSocialFacebook.text = removeHttp(photographer.facebookLink)
        } else {
            view.viewFacebook.visibility = View.GONE
            view.llFacebook.visibility = View.GONE
            view.tvSocialFacebook.text = "(not available)"
        }

        if (!TextUtils.isEmpty(photographer.instagramLink)) {
            view.tvSocialInstagram.text = removeHttp(photographer.instagramLink)
        } else {
            view.viewInstagram.visibility = View.GONE
            view.llInstagram.visibility = View.GONE
            view.tvSocialInstagram.text = "(not available)"
        }

        if (!TextUtils.isEmpty(photographer.twitterLink)) {
            view.tvSocialTwitter.text = removeHttp(photographer.twitterLink)
        } else {
            view.viewTwitter.visibility = View.GONE
            view.llTwitter.visibility = View.GONE
            view.tvSocialTwitter.text = "(not available)"
        }

        if (!TextUtils.isEmpty(photographer.pinterestLink)) {
            view.tvSocialPintreset.text = removeHttp(photographer.pinterestLink)
        } else {
            view.viewPintreset.visibility = View.GONE
            view.llPintrest.visibility = View.GONE
            view.tvSocialPintreset.text = "(not available)"
        }


    }

    fun removeHttp(url: String): String {
        return url.replace("https://", "")
    }

    internal fun displayDetails(photographer: Photographer) {

        /*tvPersonName.setText(photographer.clName);

        if (!TextUtils.isEmpty(photographer.clMobile)) {
            tvMobile.setText(photographer.clMobile);
        } else {
            tvMobile.setText("-");
        }

        if (!TextUtils.isEmpty(photographer.clEmail)) {
            tvEmail.setText(photographer.clEmail);
        } else {
            tvEmail.setText("-");
        }
        if (!TextUtils.isEmpty(photographer.clAddress)) {
            tvAddress.setText(photographer.clAddress);
        } else {
            tvAddress.setText("-");
        }
        tvAbout.setText(photographer.clAbouts);

        if (!TextUtils.isEmpty(photographer.clLogo)) {
            Picasso.with(this)
                    .load(photographer.logoUrl)
                    .into(ivUserProfile, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            ivUserProfile.setImageResource(R.drawable.ic_svg_user_thumb_128dp);
                        }
                    });
        } else {
            ivUserProfile.setImageResource(R.drawable.ic_svg_user_thumb_128dp);
        }*/

    }

    companion object {
        private val TAG = FragPhotographer::class.java.simpleName
    }
}
