package com.app.photobook.frag.Portfolio

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.LocalBroadcastManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.photobook.CustomApp
import com.app.photobook.R
import com.app.photobook.model.PortfolioRes
import com.app.photobook.model.User
import com.app.photobook.retro.RetroApi
import com.app.photobook.tools.Constants
import com.app.photobook.tools.MyPrefManager
import com.app.photobook.tools.Utils
import com.app.photobook.ui.MainActivity
import kotlinx.android.synthetic.main.frag_portfolio_home.view.*
import kotlinx.android.synthetic.main.navigation_toolbar.view.*
import org.apache.http.HttpStatus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragHome : Fragment() {

    internal lateinit var retroApi: RetroApi

    internal lateinit var pagerAdapter: PagerClass
    internal lateinit var myPrefManager: MyPrefManager
    internal var user: User? = null

    internal var isDetached = false
    internal lateinit var myView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        myView = inflater.inflate(R.layout.frag_portfolio_home, container, false)

        myPrefManager = MyPrefManager(activity)
        user = myPrefManager.userDetails

        //myView.tvEmptyMsg.typeface = CustomApp.getFontNormal()

        var title = "Portfolio"
        if ((activity as MainActivity).photographer != null &&
                !TextUtils.isEmpty((activity as MainActivity).photographer.portfolioLabel)) {
            title = (activity as MainActivity).photographer.portfolioLabel
        }
        myView.toolbar.title = title

        val app = activity!!.application as CustomApp
        retroApi = app.retroApi

        initPager()
        //initSwipView()

        myView.postDelayed({
            fetchAlbums()
        }, 100)


        return myView
    }

    /*internal fun initSwipView() {
        //to change the color of the refresh indictor
        myView.swipeLayout!!.setOnRefreshListener(onRefreshListener)
        myView.swipeLayout!!.setColorSchemeColors(ContextCompat.getColor(context, R.color.color1),
                ContextCompat.getColor(context, R.color.color2),
                ContextCompat.getColor(context, R.color.color3),
                ContextCompat.getColor(context, R.color.color4))
    }*/

    /*internal var onRefreshListener: SwipeRefreshLayout.OnRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        fetchAlbums()
    }*/

    private fun fetchAlbums() {

        if (isDetached) {
            return
        }

        if (!Utils.isOnline(context)) {
            //Utils.showNoInternetMessage(activity, myView.llProgress)
            myView.llProgress.visibility = View.GONE
            sendEmptyBroadcast()
            return
        }

        //myView.swipeLayout.isRefreshing = true
        myView.llProgress.visibility = View.VISIBLE

        val responseBodyCall = retroApi.portfolio
        responseBodyCall.enqueue(object : Callback<PortfolioRes> {

            override fun onResponse(call: Call<PortfolioRes>?, response: Response<PortfolioRes>?) {
                if (isDetached) {
                    return
                }

                ///myView.swipeLayout.isRefreshing = false
                myView.llProgress.visibility = View.GONE

                if (response!!.code() != HttpStatus.SC_OK) {
                    Log.e(TAG, response.errorBody().string())
                    sendEmptyBroadcast()
                    return
                }

                var hasAlbums = false
                var hasVideos = false

                var res = response.body()
                if (res.error == 0) {
                    if (res.data != null) {

                        if (res.data!!.albums != null && res.data!!.albums.isNotEmpty()) {
                            var intent = Intent(Constants.ACTION_UPDATE_ALBUMS_PORTFOLIO)
                            intent.putExtra("albums", res.data!!.albums)
                            LocalBroadcastManager.getInstance(activity!!)
                                    .sendBroadcast(intent)
                            hasAlbums = true
                        }

                        if (res.data!!.videos != null && res.data!!.videos.isNotEmpty()) {
                            var intent = Intent(Constants.ACTION_UPDATE_VIDEO_PORTFOLIO)
                            intent.putExtra("videos", res.data!!.videos)
                            sendBroadcast(intent)
                            hasVideos = true
                        }
                    }
                }

                if (!hasAlbums) {
                    var intent = Intent(Constants.ACTION_UPDATE_ALBUMS_PORTFOLIO)
                    sendBroadcast(intent)
                }

                if (!hasVideos) {
                    var intent = Intent(Constants.ACTION_UPDATE_VIDEO_PORTFOLIO)
                    sendBroadcast(intent)
                }

            }

            override fun onFailure(call: Call<PortfolioRes>?, t: Throwable?) {
                if (isDetached) {
                    return
                }
                //myView.swipeLayout.isRefreshing = false
                myView.llProgress.visibility = View.GONE
                sendEmptyBroadcast()
                t!!.printStackTrace()
            }
        })
    }

    private fun sendEmptyBroadcast() {
        var intent = Intent(Constants.ACTION_UPDATE_ALBUMS_PORTFOLIO)
        sendBroadcast(intent)

        intent = Intent(Constants.ACTION_UPDATE_VIDEO_PORTFOLIO)
        sendBroadcast(intent)
    }

    fun sendBroadcast(intent: Intent) {
        LocalBroadcastManager.getInstance(activity!!)
                .sendBroadcast(intent)
    }

    fun initPager() {
        pagerAdapter = PagerClass()
        myView.pager.adapter = pagerAdapter
        myView.tabs.setupWithViewPager(myView.pager)
    }

    inner class PagerClass : FragmentStatePagerAdapter(childFragmentManager) {

        var titles = arrayOf("Photography", "Videos")
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> {
                    FragAlbums()
                }
                else -> {
                    FragVideos()
                }
            }
        }

        override fun getCount(): Int {
            return titles.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return titles[position]
        }
    }

    override fun onDetach() {
        super.onDetach()
        isDetached = true
    }

    companion object {
        private val TAG = FragHome::class.java.simpleName
    }
}
