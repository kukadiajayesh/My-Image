package com.app.photobook.tools;

import android.content.Context;
import android.content.SharedPreferences;

import com.app.photobook.R;

/**
 * Created by Jayesh on 11/18/2017.
 */

public class ThemeManager {

    // Shared Preferences reference
    SharedPreferences pref;

    // Editor reference for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file category_name
    private static final String PREFER_NAME = "theme_manager";
    public static final String KEY_THEME = "theme";

    public static final int TYPE_NO_ACTION_BAR = 1;

    public static final int THEME_BROWN_ID = 0;
    public static final int THEME_RED_ID = 1;
    public static final int THEME_PINK_ID = 2;
    public static final int THEME_PURPLE_ID = 3;
    public static final int THEME_DEEP_PURPLE_ID = 4;
    public static final int THEME_INDIGO_ID = 5;
    public static final int THEME_BLUE_ID = 6;
    public static final int THEME_LIGHT_BLUE_ID = 7;
    public static final int THEME_CYAN_ID = 8;
    public static final int THEME_GREEN_ID = 9;
    public static final int THEME_LIME_ID = 10;
    public static final int THEME_AMBER_ID = 11;
    public static final int THEME_DEEP_ORANGE_ID = 12;
    public static final int THEME_TEAL_ID = 13;

    private int THEME_BROWN = R.style.AppTheme_brown;
    private int THEME_BROWN_NOACTIONBAR = R.style.AppTheme_brown_NoActionbar;

    private int THEME_RED = R.style.AppTheme_red;
    private int THEME_RED_NOACTIONBAR = R.style.AppTheme_red_NoActionbar;

    private int THEME_PINK = R.style.AppTheme_pink;
    private int THEME_PINK_NOACTIONBAR = R.style.AppTheme_pink_NoActionbar;

    private int THEME_PURPLE = R.style.AppTheme_purple;
    private int THEME_PURPLE_NOACTIONBAR = R.style.AppTheme_purple_NoActionbar;

    private int THEME_DEEP_PURPLE = R.style.AppTheme_deep_purple;
    private int THEME_DEEP_PURPLE_NOACTIONBAR = R.style.AppTheme_deep_purple_NoActionbar;

    private int THEME_INDIGO = R.style.AppTheme_indigo;
    private int THEME_INDIGO_NOACTIONBAR = R.style.AppTheme_indigo_NoActionbar;

    private int THEME_BLUE = R.style.AppTheme_blue;
    private int THEME_BLUE_NOACTIONBAR = R.style.AppTheme_blue_NoActionbar;

    private int THEME_LIGHT_BLUE = R.style.AppTheme_light_blue;
    private int THEME_LIGHT_BLUE_NOACTIONBAR = R.style.AppTheme_light_blue_NoActionbar;

    private int THEME_CYAN = R.style.AppTheme_cyan;
    private int THEME_CYAN_NOACTIONBAR = R.style.AppTheme_cyan_NoActionbar;

    private int THEME_GREEN = R.style.AppTheme_green;
    private int THEME_GREEN_NOACTIONBAR = R.style.AppTheme_green_NoActionbar;

    private int THEME_LIME = R.style.AppTheme_lime;
    private int THEME_LIME_NOACTIONBAR = R.style.AppTheme_lime_NoActionbar;

    private int THEME_AMBER = R.style.AppTheme_amber;
    private int THEME_AMBER_NOACTIONBAR = R.style.AppTheme_amber_NoActionbar;

    private int THEME_DEEP_ORANGE = R.style.AppTheme_deep_orange;
    private int THEME_DEEP_ORANGE_NOACTIONBAR = R.style.AppTheme_deep_orange_NoActionbar;

    private int THEME_TEAL = R.style.AppTheme_teal;
    private int THEME_TEAL_NOACTIONBAR = R.style.AppTheme_teal_NoActionbar;


    // Constructor
    public ThemeManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);

    }

    public void createOrUpdateTheme(int theme) {
        editor = pref.edit();
        editor.putInt(KEY_THEME, theme);
        editor.commit();
    }

    public int getThemeId() {
        return pref.getInt(KEY_THEME, THEME_BROWN_ID);
    }

    public int getTheme() {
        return getTheme(0);
    }

    public int getTheme(int type) {
        int themeId = pref.getInt(KEY_THEME, THEME_BROWN_ID);
        return getTheme(type, themeId);
    }


    public int getTheme(int type, int id) {
        int themeId = id;//pref.getInt(KEY_THEME, THEME_BROWN_ID);
        int theme;

        switch (themeId) {
            case THEME_BROWN_ID:
                if (type == TYPE_NO_ACTION_BAR) {
                    theme = THEME_BROWN_NOACTIONBAR;
                } else {
                    theme = THEME_BROWN;
                }
                break;
            case THEME_RED_ID:
                if (type == TYPE_NO_ACTION_BAR) {
                    theme = THEME_RED_NOACTIONBAR;
                } else {
                    theme = THEME_RED;
                }
                break;
            case THEME_PINK_ID:
                if (type == TYPE_NO_ACTION_BAR) {
                    theme = THEME_PINK_NOACTIONBAR;
                } else {
                    theme = THEME_PINK;
                }
                break;
            case THEME_PURPLE_ID:
                if (type == TYPE_NO_ACTION_BAR) {
                    theme = THEME_PURPLE_NOACTIONBAR;
                } else {
                    theme = THEME_PURPLE;
                }
                break;
            case THEME_DEEP_PURPLE_ID:
                if (type == TYPE_NO_ACTION_BAR) {
                    theme = THEME_DEEP_PURPLE_NOACTIONBAR;
                } else {
                    theme = THEME_DEEP_PURPLE;
                }
                break;
            case THEME_INDIGO_ID:
                if (type == TYPE_NO_ACTION_BAR) {
                    theme = THEME_INDIGO_NOACTIONBAR;
                } else {
                    theme = THEME_INDIGO;
                }
                break;
            case THEME_BLUE_ID:
                if (type == TYPE_NO_ACTION_BAR) {
                    theme = THEME_BLUE_NOACTIONBAR;
                } else {
                    theme = THEME_BLUE;
                }
                break;
            case THEME_LIGHT_BLUE_ID:
                if (type == TYPE_NO_ACTION_BAR) {
                    theme = THEME_LIGHT_BLUE_NOACTIONBAR;
                } else {
                    theme = THEME_LIGHT_BLUE;
                }
                break;
            case THEME_CYAN_ID:
                if (type == TYPE_NO_ACTION_BAR) {
                    theme = THEME_CYAN_NOACTIONBAR;
                } else {
                    theme = THEME_CYAN;
                }
                break;
            case THEME_GREEN_ID:
                if (type == TYPE_NO_ACTION_BAR) {
                    theme = THEME_GREEN_NOACTIONBAR;
                } else {
                    theme = THEME_GREEN;
                }
                break;
            case THEME_LIME_ID:
                if (type == TYPE_NO_ACTION_BAR) {
                    theme = THEME_LIME_NOACTIONBAR;
                } else {
                    theme = THEME_LIME;
                }
                break;
            case THEME_AMBER_ID:
                if (type == TYPE_NO_ACTION_BAR) {
                    theme = THEME_AMBER_NOACTIONBAR;
                } else {
                    theme = THEME_AMBER;
                }
                break;
            case THEME_DEEP_ORANGE_ID:
                if (type == TYPE_NO_ACTION_BAR) {
                    theme = THEME_DEEP_ORANGE_NOACTIONBAR;
                } else {
                    theme = THEME_DEEP_ORANGE;
                }
                break;

            case THEME_TEAL_ID:
                if (type == TYPE_NO_ACTION_BAR) {
                    theme = THEME_TEAL_NOACTIONBAR;
                } else {
                    theme = THEME_TEAL;
                }
                break;

            default:
                if (type == TYPE_NO_ACTION_BAR) {
                    theme = THEME_BROWN_NOACTIONBAR;
                } else {
                    theme = THEME_BROWN;
                }
        }

        return theme;
    }

}
