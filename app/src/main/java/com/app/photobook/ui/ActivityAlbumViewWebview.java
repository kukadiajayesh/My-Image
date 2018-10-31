package com.app.photobook.ui;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.photobook.CustomApp;
import com.app.photobook.R;
import com.app.photobook.dialog.CommentDialog;
import com.app.photobook.model.Album;
import com.app.photobook.model.AlbumImage;
import com.app.photobook.retro.RetroApi;
import com.app.photobook.tools.BitmapUtils;
import com.app.photobook.tools.MyPrefManager;
import com.app.photobook.tools.SharingUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityAlbumViewWebview extends BaseActivity {

    private static final String TAG = ActivityAlbumViewWebview.class.getSimpleName();
    @BindView(R.id.webview)
    WebView webview;

    @BindView(R.id.llData)
    RelativeLayout llData;
    @BindView(R.id.ivWifi)
    ImageView ivWifi;
    @BindView(R.id.tvEmptyMsg)
    TextView tvEmptyMsg;
    @BindView(R.id.btnRetry)
    Button btnRetry;
    @BindView(R.id.container)
    RelativeLayout container;

    @BindView(R.id.llLeftComment)
    View llLeftComment;
    @BindView(R.id.llRightComment)
    View llRightComment;

    Handler handler = new Handler();

    Album album;
    ArrayList<AlbumImage> albumImages;

    CommentDialog commentDialog;
    int mCurrentPos = 1;
    @BindView(R.id.tvLeftComment)
    TextView tvLeftComment;
    @BindView(R.id.tvRightComment)
    TextView tvRightComment;

    @BindView(R.id.ivPrev)
    ImageView ivPrev;
    @BindView(R.id.ivNext)
    ImageView ivNext;
    @BindView(R.id.llShare)
    View llShare;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.dialogLayout)
    LinearLayout dialogLayout;
    @BindView(R.id.ivMusic)
    ImageView ivMusic;

    MediaPlayer mediaPlayer;

    boolean isSinglePage = false;
    int widthPixels, heightPixels;

    private String shareText = "";

    boolean mAutoPlay = false;
    final int SLIDE_DELAY = 3000;
    MyPrefManager myPrefManager;

    boolean liveMode = false;
    boolean isOffline = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_album_view_webview);
        ButterKnife.bind(this);

        if (getIntent().hasExtra("album")) {
            album = getIntent().getParcelableExtra("album");
            albumImages = album.images;
            isOffline = album.isOffline == 1;
        }

        myPrefManager = new MyPrefManager(this);
        initMediaPlayer();

        if (getIntent().hasExtra("auto_play")) {
            mAutoPlay = getIntent().getBooleanExtra("auto_play", false);
        }

        if (getIntent() != null && getIntent().hasExtra("live_mode")) {
            liveMode = getIntent().getBooleanExtra("live_mode", false);
        }

        CustomApp app = (CustomApp) getApplication();
        RetroApi retroApi = app.getRetroApi();

        /*shareText = String.format(getString(R.string.share_text), album.eventName,
                album.eventPassword);*/

        shareText = album.shareMessage;

        /*shareText = "Hello Friends, View my Photobook " + album.pbName + "\n" +
                "Download the app from: "
                + app.getConstants().getSHARE_URL() + " & use Pin : " + album.pbPassword;*/

        widthPixels = getResources().getDisplayMetrics().widthPixels;
        heightPixels = getResources().getDisplayMetrics().heightPixels;

        commentDialog = new CommentDialog(this, retroApi, dialogLayout);

        llLeftComment.setOnClickListener(onClickListener);
        llRightComment.setOnClickListener(onClickListener);

        loadWebView();


        //Init_Ui_Album_View();
        //startSlideShow();
    }

    private void initMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this, R.raw.christmas_coming);
        mediaPlayer.setLooping(true);
        TypedValue typedValue = new TypedValue();
        getResources().getValue(R.dimen.background_sound_vol, typedValue, true);
        float volume = typedValue.getFloat();
        mediaPlayer.setVolume(volume, volume);
        setIconByMusic();
    }

    void setIconByMusic() {
        if (myPrefManager.getMusicStatus()) {
            ivMusic.setImageResource(R.drawable.ic_music_on);
        } else {
            ivMusic.setImageResource(R.drawable.ic_music_off);
        }
    }

    void playMusic() {
        if (myPrefManager.getMusicStatus()) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        playMusic();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    private void changeVisibilityByPos() {

        if (mCurrentPos == 1) {
            llRightComment.setVisibility(View.GONE);
            ivPrev.setVisibility(View.GONE);
            llShare.setVisibility(View.GONE);
        } else {

            int lastPage = albumImages.size();

            if (mCurrentPos == lastPage) {

                if (albumImages.size() % 2 == 0) {
                    llRightComment.setVisibility(View.GONE);
                }

                ivNext.setVisibility(View.GONE);
                if (!liveMode && album.isSharble == 1) {
                    llShare.setVisibility(View.VISIBLE);
                }
            } else {

                ivPrev.setVisibility(View.VISIBLE);
                ivNext.setVisibility(View.VISIBLE);

                if (!mAutoPlay && !liveMode) {
                    //tvLeftComment.setText("Comment (" + mCurrentPos + ")");
                    llLeftComment.setVisibility(View.VISIBLE);
                    llRightComment.setVisibility(View.VISIBLE);
                }
                llShare.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int pos = mCurrentPos;

            if (v.getId() == R.id.llLeftComment) {

                if (pos == 1) {
                    pos = 0;
                } else {
                    pos--;
                }

                //Toast.makeText(ActivityAlbumViewWebview.this, pos + "", Toast.LENGTH_SHORT).show();

                AlbumImage albumImage = albumImages.get(pos);
                //Log.e(TAG, "albumImageBck.id: " + albumImage.pageId);


                commentDialog.show(albumImage.albumId, albumImage.pageId);

            } else if (v.getId() == R.id.llRightComment) {

                //pos++;
                if (pos > albumImages.size() - 1) {
                    pos = 0;
                }
                //Toast.makeText(ActivityAlbumViewWebview.this, pos + "", Toast.LENGTH_SHORT).show();

                AlbumImage albumImage = albumImages.get(pos);
                //Log.e(TAG, "albumImageBck.id: " + albumImageBck.pageId);

                commentDialog.show(albumImage.albumId, albumImage.pageId);

            }

            //Log.e(TAG, "onClick: " + mCurrentPos);

        }
    };

    void loadWebView() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            webview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        } else {
            // older android version, disable hardware acceleration
            webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        //webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setDisplayZoomControls(false);

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                //webview.loadUrl("javascript:ap_next()");
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e(TAG, "onReceivedError: " + error.toString());
            }
        });

        //webview.loadUrl("file:///android_asset/sample.html");
        //webview.loadUrl("file:///android_asset/turnjs4/samples/double-page/index.html");
        //webview.loadUrl("file:///android_asset/index.html?path=file:///sdcard/turn/&total=5");
        //String extraParams = "path=file:///sdcard/turn/&total=26&auto_play=0&width=1200&height=400";

        int total = album.totalImg;
        Log.e(TAG, "total Images: " + total);

        String extraParams = "path=file:///" + album.localPath + "&total=" + total +
                "&width=" + album.pb_width + "&height=" + album.pb_height + "&auto_play=";

        if (liveMode || !isOffline) {
            extraParams = "path=" + album.path + "&total=" + total +
                    "&width=" + album.pb_width + "&height=" + album.pb_height + "&auto_play=";
        }


        if (mAutoPlay) {

            llLeftComment.setVisibility(View.GONE);
            llRightComment.setVisibility(View.GONE);
            ivNext.setVisibility(View.GONE);

            extraParams += "1";
        } else {
            extraParams += "0";
        }

        //webview.setWebChromeClient(new WebChromeClient());

        //TODO: uncomment this
        webview.loadUrl("file:///android_asset/webview_4/index.html?" + extraParams);
        webview.addJavascriptInterface(new JavaScriptInterface(), "app");

        //TODO: Remove this
        //webview.loadUrl("http://glimpsephotobook.com/webview");
    }

    private class JavaScriptInterface {

        @JavascriptInterface
        public void callFromJS(int pos) {
            //Toast.makeText(ActivityAlbumViewWebview.this, "JavaScript interface call: " + pos, Toast.LENGTH_LONG).show();
            Log.e(TAG, "callFromJS: " + pos);
            mCurrentPos = pos;

            if (!mAutoPlay) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        changeVisibilityByPos();
                    }
                });
            }
        }

        @JavascriptInterface
        public void onLoadSuccess() {
            if (!mAutoPlay) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!liveMode) {
                            llLeftComment.setVisibility(View.VISIBLE);
                        }
                        ivNext.setVisibility(View.VISIBLE);
                    }
                });
            }

        }
    }

    @OnClick({R.id.ivPrev, R.id.ivNext, R.id.ivWhatsapp, R.id.ivFacebook,
            R.id.ivGoogle, R.id.ivTwitter, R.id.ivMail, R.id.ivMore, R.id.ivMusic})
    public void onViewClicked(View view) {

        /*Uri uri = null;
        if (!liveMode) {
            *//*uri = Uri.parse(albumImages.get(0).localFilePath);*//*
            uri = FileProvider.getUriForFile(ActivityAlbumViewWebview.this,
                    getPackageName() + ".fileprovider", new File(albumImages.get(0).localFilePath));  // use this version for API >= 24
        }*/

        File file = null;
        if (!liveMode && isOffline) {
            file = new File(albumImages.get(0).localFilePath);
        }

        switch (view.getId()) {

            case R.id.ivWhatsapp:
                share(SharingUtils.PACKAGE_WHATSAPP);
                break;
            case R.id.ivFacebook:
                share(SharingUtils.PACKAGE_FB);
                break;
            case R.id.ivGoogle:
                share(SharingUtils.PACKAGE_GPLUS);
                break;
            case R.id.ivTwitter:
                share(SharingUtils.PACKAGE_TWITTER);
                /*SharingUtils.sharingToSocialMedia(ActivityAlbumViewWebview.this,
                        SharingUtils.PACKAGE_TWITTER, shareText, file);*/
                break;
            case R.id.ivMail:
                share(SharingUtils.PACKAGE_MAIL);
                //SharingUtils.sharingToMail(ActivityAlbumViewWebview.this, shareText, file);
                break;
            case R.id.ivMore:
                share(SharingUtils.PACKAGE_OTHER);
                /*try {
                    SharingUtils.shareAlbum(ActivityAlbumViewWebview.this, shareText, file);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                break;
            case R.id.ivPrev:
                webview.loadUrl("javascript:ap_previous()");
                break;
            case R.id.ivNext:
                webview.loadUrl("javascript:ap_next()");
                break;
            case R.id.ivMusic:
                myPrefManager.storeMusicStatus(!myPrefManager.getMusicStatus());
                setIconByMusic();

                if (myPrefManager.getMusicStatus()) {
                    mediaPlayer.start();
                } else {
                    mediaPlayer.pause();
                }
                break;
        }
    }

    void share(final String type) {

        File file;
        if (!liveMode && isOffline) {
            try {
                file = new File(albumImages.get(0).localFilePath);
                shareOffline(type, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Glide.with(this)
                    .asBitmap()
                    .load(album.images.get(0).url)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            File file = BitmapUtils.getBitmapFromDrawable(ActivityAlbumViewWebview.this, resource);
                            try {
                                shareOffline(type, file);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }

    }

    private void shareOffline(String type, File file) throws Exception {
        if (type.equals(SharingUtils.PACKAGE_MAIL)) {
            SharingUtils.sharingToMail(ActivityAlbumViewWebview.this, shareText, file);
        } else if (type.equals(SharingUtils.PACKAGE_OTHER)) {
            SharingUtils.shareAlbum(ActivityAlbumViewWebview.this, shareText, file);
        } else {
            try {
                SharingUtils.sharingToSocialMedia(ActivityAlbumViewWebview.this,
                        type, shareText, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
