package com.app.photobook.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.app.photobook.R
import com.jaedongchicken.ytplayer.YoutubePlayerView
import com.jaedongchicken.ytplayer.model.YTParams




class YoutubeActivity : AppCompatActivity() {

    var URL = "http://www.igotmywork.com/login.aspx"
    lateinit var youtubePlayerView: YoutubePlayerView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube)

        if (intent != null && intent.hasExtra("url")) {
            URL = intent.getStringExtra("url")
        }


        // get id from XML
        youtubePlayerView = findViewById<View>(R.id.youtubePlayerView) as YoutubePlayerView

        // Control values
        // see more # https://developers.google.com/youtube/player_parameters?hl=en
        val params = YTParams()
        //params.setControls(1); // hide control
        //params.setVolume(100); // volume control
        //params.setPlaybackQuality(PlaybackQuality.small); // video quality control
        params.autoplay = 1
        params.autohide = 1
        params.rel = 0

        // initialize YoutubePlayerCallBackListener with Params and VideoID
        //youtubePlayerView.initialize("WCchr07kLPE", params, YoutubePlayerView.YouTubeListener())

        // initialize YoutubePlayerCallBackListener with Params and Full Video URL
        // To Use - avoid UMG block!!!! but you'd better make own your server for your real service.
        // youtubePlayerView.initializeWithCustomURL("p1Zt47V3pPw" or "http://jaedong.net/youtube/p1Zt47V3pPw", params, new YoutubePlayerView.YouTubeListener())

        // make auto height of youtube. if you want to use 'wrap_content'
        youtubePlayerView.setAutoPlayerHeight(this)
        // initialize YoutubePlayerCallBackListener and VideoID
        youtubePlayerView.initialize(URL, object : YoutubePlayerView.YouTubeListener {

            override fun onReady() {
                // when player is ready.
                youtubePlayerView.play()
            }

            override fun onStateChange(state: YoutubePlayerView.STATE) {
                /**
                 * YoutubePlayerView.STATE
                 *
                 * UNSTARTED, ENDED, PLAYING, PAUSED, BUFFERING, CUED, NONE
                 *
                 */
            }

            override fun onPlaybackQualityChange(arg: String) {}

            override fun onPlaybackRateChange(arg: String) {}

            override fun onError(error: String) {}

            override fun onApiChange(arg: String) {}

            override fun onCurrentSecond(second: Double) {
                // currentTime callback
            }

            override fun onDuration(duration: Double) {
                // total duration
            }

            override fun logs(log: String) {
                // javascript debug log. you don't need to use it.
            }
        })


        // psuse video
        youtubePlayerView.pause()
        // play video when it's ready
        youtubePlayerView.play()

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        // pause video when on the background mode.
        youtubePlayerView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // this is optional but you need.
        youtubePlayerView.destroy()
    }
}
