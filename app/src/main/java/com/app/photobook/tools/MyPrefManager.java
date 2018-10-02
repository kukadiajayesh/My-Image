package com.app.photobook.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.app.photobook.model.User;
import com.google.gson.Gson;

public class MyPrefManager {

    // Shared Preferences reference
    SharedPreferences pref;

    // Editor reference for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file category_name
    private static final String PREFER_NAME = "user_details";
    public static final String KEY_USER_JSON = "key_user_json";
    public static final String KEY_USER_TOKEN = "token";
    public static final String KEY_MUSIC_STATUS = "music";

    // Constructor
    public MyPrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);

    }

    // Create login session
    public void createOrUpdateUserDetails(User user) {

        Gson gson = new Gson();
        String json = gson.toJson(user);

        editor = pref.edit();
        editor.putString(KEY_USER_JSON, json);
        editor.commit();
    }


    /**
     * Get stored session data
     */
    public User getUserDetails() {

        String json = pref.getString(KEY_USER_JSON, null);
        Gson gson = new Gson();
        if (json == null)
            return null;
        else
            return gson.fromJson(json, User.class);
    }

    /**
     * Clear session details
     */
    public void logoutUser() {

        editor = pref.edit();
        // Clearing all user data from Shared Preferences
        editor.remove(KEY_USER_JSON);
        editor.commit();
    }

    public void storeToken(String token) {
        editor = pref.edit();
        editor.putString(KEY_USER_TOKEN, token);
        editor.commit();
    }

    public String getToken() {
        return pref.getString(KEY_USER_TOKEN, "");
    }


    public void storeMusicStatus(boolean value) {
        editor = pref.edit();
        editor.putBoolean(KEY_MUSIC_STATUS, value);
        editor.commit();
    }

    public boolean getMusicStatus() {
        return pref.getBoolean(KEY_MUSIC_STATUS, true);
    }


    // Check for login
    public boolean isUserLoggedIn() {
        return pref.contains(KEY_USER_JSON);
    }

    public SharedPreferences getPref() {
        return pref;
    }
}
