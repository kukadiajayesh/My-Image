package com.app.photobook.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.webkit.*
import com.app.photobook.R
import kotlinx.android.synthetic.main.activity_webview.*




class WebViewActivity : AppCompatActivity() {

    var URL = "http://www.igotmywork.com/login.aspx"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        // Enable Javascript
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        webSettings.pluginState = WebSettings.PluginState.ON
        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        webView.reload()

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                Log.e("onProgressChanged", newProgress.toString())
            }
        }

        // Force links and redirects to open in the WebView instead of in a browser
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return super.shouldOverrideUrlLoading(view, request)
            }
        }

        if (intent != null && intent.hasExtra("url")) {
            URL = intent.getStringExtra("url")
        }

        Log.e("url", URL)
        webView.loadUrl(URL)
    }

    fun getHTML(videoId: String): String {
        val html = ("<iframe class=\"youtube-player\" " + "style=\"border: 0; width: 100%; height: 96%;"
                + "padding:0px; margin:0px\" " + "id=\"ytplayer\" type=\"text/html\" "
                + "src=\"http://www.youtube.com/embed/" + videoId
                + "?&theme=dark&autohide=2&modestbranding=1&showinfo=0&autoplay=1\\fs=0\" frameborder=\"0\" "
                + "allowfullscreen autobuffer " + "controls onclick=\"this.play()\">\n" + "</iframe>\n")
        //LogShowHide.LogShowHideMethod("video-id from html url= ", "" + html)
        return html
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

}
