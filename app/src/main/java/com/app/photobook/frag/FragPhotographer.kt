package com.app.photobook.frag

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
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
import com.app.photobook.tools.Constants
import com.app.photobook.tools.Utils
import com.app.photobook.ui.ActivityInquiryAdd
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

        view.llCall.setOnClickListener(onClick)
        view.llWebSite.setOnClickListener(onClick)
        view.llEmail.setOnClickListener(onClick)
        view.llGoogle.setOnClickListener(onClick)
        view.llFacebook.setOnClickListener(onClick)
        view.llInstagram.setOnClickListener(onClick)
        view.llLinkedIn.setOnClickListener(onClick)
        view.llTwitter.setOnClickListener(onClick)
        view.llPintrest.setOnClickListener(onClick)
        view.llYoutube.setOnClickListener(onClick)
        view.llLocation.setOnClickListener(onClick)
        view.fabInquiry.setOnClickListener(onClick)

        return view
    }

    var onClick = View.OnClickListener {
        when (it.id) {
            R.id.llCall -> {

                if (TextUtils.isEmpty(photographer.mobile)) {
                    Toast.makeText(context, R.string.message_call_string_empty, Toast.LENGTH_SHORT).show()
                    return@OnClickListener
                }

                AlertDialog.Builder(activity!!)
                        .setMessage(photographer.mobile)
                        .setPositiveButton("Call") { dialog, which ->
                            onCallBtnClick()
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
            }
            R.id.llWebSite -> {
                openLink(photographer.website)
            }
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
            R.id.llLinkedIn -> {
                openLink(photographer.linkedinLink)
            }
            R.id.llPintrest -> {
                openLink(photographer.pinterestLink)
            }
            R.id.llYoutube -> {
                openLink(photographer.youtubeLink)
            }
            R.id.llLocation -> {
                openLink(photographer.googleMapDirection)
            }
            R.id.fabInquiry -> {
                var intent = Intent(activity!!, ActivityInquiryAdd::class.java)
                startActivity(intent)
            }
        }
    }

    private fun onCallBtnClick() {
        if (Build.VERSION.SDK_INT < 23) {
            phoneCall()
        } else {
            if (ActivityCompat.checkSelfPermission(activity!!,
                            Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                phoneCall()
            } else {
                val PERMISSIONS_STORAGE = arrayOf<String>(Manifest.permission.CALL_PHONE)
                //Asking request Permissions
                requestPermissions(PERMISSIONS_STORAGE, 9)
            }
        }
    }


    private fun phoneCall() {
        if (ActivityCompat.checkSelfPermission(activity!!,
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            val callIntent = Intent(Intent.ACTION_CALL)

            var mobile = photographer.mobile
            if (!TextUtils.isEmpty(mobile)) {
                callIntent.data = Uri.parse("tel:" + photographer.mobile)
                if (activity!!.packageManager.resolveActivity(callIntent, 0) != null) {
                    startActivity(callIntent)
                } else {
                    Toast.makeText(context, "Unable to find calling app", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity!!, "Mobile is empty", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(activity!!, "You don't assign permission.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {

            9 -> {
                var permissionGranted = false
                when (requestCode) {
                    9 -> permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                }
                if (permissionGranted) {
                    phoneCall()
                } else {
                    Toast.makeText(activity!!, "You don't assign permission.", Toast.LENGTH_SHORT).show()
                }
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
            Toast.makeText(activity!!, "Unable to open this link", Toast.LENGTH_LONG).show()
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
            Picasso.with(activity!!)
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
        view.tvCall.text = photographer.mobile
        view.tvEmail.text = photographer.email

        if (!TextUtils.isEmpty(photographer.website)) {
            view.tvWebsite.text = photographer.website
        } else {
            view.viewWebSite.visibility = View.GONE
            view.llWebSite.visibility = View.GONE
        }

        photographer.businessName = photographer.businessName.replace(" ", "")
        if (!TextUtils.isEmpty(photographer.googleplusLink)) {
            view.tvSocialGoogle.text = String.format(Constants.TITLE_GOOGLE, photographer.businessName)
        } else {
            view.viewGoogle.visibility = View.GONE
            view.llGoogle.visibility = View.GONE
        }

        if (!TextUtils.isEmpty(photographer.facebookLink)) {
            view.tvSocialFacebook.text = String.format(Constants.TITLE_FACEBOOK, photographer.businessName)
        } else {
            view.viewFacebook.visibility = View.GONE
            view.llFacebook.visibility = View.GONE
        }

        if (!TextUtils.isEmpty(photographer.instagramLink)) {
            view.tvSocialInstagram.text = String.format(Constants.TITLE_INSTAGRAM, photographer.businessName)
        } else {
            view.viewInstagram.visibility = View.GONE
            view.llInstagram.visibility = View.GONE
        }

        if (!TextUtils.isEmpty(photographer.twitterLink)) {
            view.tvSocialTwitter.text = String.format(Constants.TITLE_TWITTER, photographer.businessName)
        } else {
            view.viewTwitter.visibility = View.GONE
            view.llTwitter.visibility = View.GONE
        }

        if (!TextUtils.isEmpty(photographer.linkedinLink)) {
            view.tvLinkedIn.text = String.format(Constants.TITLE_LINKEDIN, photographer.businessName)
        } else {
            view.viewLinkedIn.visibility = View.GONE
            view.llLinkedIn.visibility = View.GONE
        }

        if (!TextUtils.isEmpty(photographer.pinterestLink)) {
            view.tvSocialPintreset.text = String.format(Constants.TITLE_PINTEREST, photographer.businessName)
        } else {
            view.viewPintreset.visibility = View.GONE
            view.llPintrest.visibility = View.GONE
        }

        if (!TextUtils.isEmpty(photographer.youtubeLink)) {
            view.tvSocialYoutube.text = String.format(Constants.TITLE_YOUTUBE, photographer.businessName)
        } else {
            view.viewYoutube.visibility = View.GONE
            view.llYoutube.visibility = View.GONE
        }

        if (!TextUtils.isEmpty(photographer.googleMapDirection)) {
            view.tvLocation.text = String.format(Constants.TITLE_GOOGLEMAP, photographer.businessName)
        } else {
            view.viewLocation.visibility = View.GONE
            view.llLocation.visibility = View.GONE
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
