package com.app.photobook.tools;

import android.content.Context;

import com.app.photobook.R;

/**
 * Created by Jayesh on 9/23/2017.
 */

public class Constants {

    Context context;

    public Constants(Context context) {
        this.context = context;

        BASE_URL = context.getString(R.string.base_url);
        /*USERNAME = context.getString(R.string.url_username);
        PASSWORD = context.getString(R.string.url_password);*/
    }

    public static final String URL_OLD = "http://albums.gulfphotobook.com/ap-api/";
    //public static final String URL = "http://design.alivefoto.com/ap-api/";
    //public static final String URL = "http://album.glimpsephotobook.com/ap-api/";
    //public static final String IMAGE_URL = URL + "uploads/user_profile/";

    //public static final String URL_REGISTER = URL + "register.php";

    public static final String ACTION_UPDATE_DATE = "action.update.dates";
    public static final String ACTION_UPDATE_ACTIVITY_USERS = "action.update.activity.users";
    public static final String ACTION_UPDATE_CLIENT_FORMS = "action.update.client_form";
    public static final String ACTION_UPDATE_NOTIFICATION = "action.update.notification";
    public static final String ACTION_UPDATE_USER_DETAILS = "action.update.user_details";
    public static final String ACTION_UPDATE_MY_POINTS = "action.update.my_points";
    public static final String ACTION_UPDATE_ALBUMS = "action.update.albums";
    public static final String ACTION_UPDATE_ALBUM_SELECTION = "action.update.album.selection";
    public static final String ACTION_UPDATE_ALBUMS_PORTFOLIO = "action.update.album.portfolio";
    public static final String ACTION_UPDATE_VIDEO_PORTFOLIO = "action.update.video.portfolio";

    public static final String email = "email";
    public static final String BLANK_SPACES = "          ";
    public static final String BLANK_SPACES_COMMENT = "                    ";

    /*public static final String USERNAME = "appuser";
    public static final String PASSWORD = "Appuser@Design";
    public static final String PASSWORD_OLD = "Appuser@Gulfphotobook";*/

    public static final String DATABASE_NAME = "photo_book";

    public static final String ACTIVITY = "activity";

    public static final int WIDTH = 2048;
    public static final int HEIGHT = 2048;

    public static final int REQUEST_CODE_PERMISSION = 101;

    public static final int MAX_DISPLAY_VALUE = 5;

    public static final String NOTIFY_ID = "notify_id";
    public static final String NOTIFY_TYPE = "notify_type";
    public static final String FRAG_EXTRA = "extra";

    public static final String RESPONSE_TYPE = "response_type";
    public static final String RESPONSE_DETAILS = "details";

    public static final int FRAG_DASHBOARD = 0;
    public static final int FRAG_ACTIVITY = 1;
    public static final int FRAG_REWARD = 2;
    public static final int FRAG_REVIEW_ACTIVITY = 3;
    public static final int FRAG_REVIEW_REWARD = 4;
    public static final int FRAG_PROFILE = 5;
    public static final int FRAG_CHANGE_PWD = 6;

    public static final String TYPE_ACTIVITY_STATUS_UPDATE = "activity_status_update";
    public static final String TYPE_ACTIVITY_REVIEW_UPDATE = "activity_review";
    public static final String TYPE_REWARD_STATUS_UPDATE = "reward_status_update";
    public static final String TYPE_REWARD_REVIEW_UPDATE = "reward_review";

    public static final String TITLE_ACTIVITY_STATUS_CHANGE = "Activity Status Changed";
    public static final String TITLE_ACTIVITY_STATUS_REVIEWED = "Activity Review Changed";
    public static final String TITLE_REWARD_STATUS_CHANGE = "Reward Status Changed";
    public static final String TITLE_REWARD_STATUS_REVIEWED = "Reward Review Changed";


    public static final int STATUS_REQUESTING = 0;
    public static final int STATUS_REVIEW_PENDING = 0;
    public static final int STATUS_PENDING = 1;
    public static final int STATUS_APPROVED = 2;
    public static final int STATUS_REJECTED = 3;
    public static final int STATUS_COMPLETED = 4;
    public static final int STATUS_REISSUING = 5;
    public static final int STATUS_RECOMPLETED = 6;

    public static final String STATUS_REQUESTING_STR = "Requesting";
    public static final String STATUS_REVIEW_PENDING_STR = "Review Pending";
    public static final String STATUS_PENDING_STR = "Pending";
    public static final String STATUS_APPROVED_STR = "Approved";
    public static final String STATUS_REJECTED_STR = "Rejected";
    public static final String STATUS_COMPLETED_STR = "Completed";
    public static final String STATUS_REISSUING_STR = "Reissuing";
    public static final String STATUS_RECOMPLETED_STR = "Re-Completed";
    public static final String STATUS_RECLAIMED_STR = "Re-Claimed";


    public static final String GALLERY_TYPE_ALBUM = "album_gallery";
    public static final String GALLERY_TYPE_SELECTION = "selection_gallery";
    public static final String GALLERY_TYPE_IMAGE = "photo_gallery";

    public static final int STATUS_NOTIFICATION_UNREAD = 0;
    public static final int STATUS_NOTIFICATION_READ = 1;

    public static final int VIDEO_TYPE_YOUTUBE = 1;
    public static final int VIDEO_TYPE_VIMEO = 2;

    public static final String YOUTUBE_THUMB_VIEW = "https://img.youtube.com/vi/#ID/hqdefault.jpg";
    public static final String VIMEO_THUMB_VIEW = "http://vimeo.com/api/v2/video/#ID.json";
    public static final String VIDEO_PLAY_IFRAME = "<iframe width=\"100%\" height=\"100%\" " +
            "src=\"#src\" frameborder=\"0\" allow=\"autoplay; encrypted-media\"  webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>";

    private String BASE_URL = "";

    /*private String USERNAME = "";
    private String PASSWORD = "";*/
    //private String PASSWORD_OLD = "Appuser@Gulfphotobook";

    public String getBaseUrl() {
        return BASE_URL;
    }

    /*public String getUsername() {
        return USERNAME;
    }

    public String getPassword() {
        return PASSWORD;
    }*/

}
