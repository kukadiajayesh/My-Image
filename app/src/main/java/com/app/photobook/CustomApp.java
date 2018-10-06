package com.app.photobook;

import android.app.Application;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.photobook.retro.RetroApi;
import com.app.photobook.retro.ServiceGenerator;
import com.app.photobook.room.RoomDatabaseClass;
import com.app.photobook.tools.Constants;

/**
 * Created by Jayesh on 11/3/2017.
 */

public class CustomApp extends Application {

    private static final String TAG = CustomApp.class.getSimpleName();
    private static Typeface fontNormal, fontBold, fontItalic;
    private static RetroApi retroApi;
    private static RoomDatabaseClass roomDatabaseClass;
    private static Constants constants;

    @Override
    public void onCreate() {
        super.onCreate();

        constants = new Constants(this);

        fontNormal = Typeface.createFromAsset(getAssets(), "font/Segoe_UI.ttf");
        fontBold = Typeface.createFromAsset(getAssets(), "font/Segoe_UI_Bold.ttf");
        fontItalic = Typeface.createFromAsset(getAssets(), "font/segoeui_italic.ttf");

        roomDatabaseClass = Room.databaseBuilder(this, RoomDatabaseClass.class, Constants.DATABASE_NAME)
                .allowMainThreadQueries()
                .addMigrations(MIGRATION_1_2)
                .addMigrations(MIGRATION_2_3)
                .build();
    }

    Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Album ADD COLUMN path TEXT");
            Log.e(TAG, "migrate: 1 -> 2");
        }
    };

    Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Photographer ADD COLUMN portfolioLabel TEXT");
            database.execSQL("ALTER TABLE Photographer ADD COLUMN privateGalleryLabel TEXT");
            database.execSQL("ALTER TABLE Photographer ADD COLUMN googleMapDirection TEXT");
            Log.e(TAG, "migrate: 2 -> 3");
        }
    };

    public static RoomDatabaseClass getRoomDatabaseClass() {
        return roomDatabaseClass;
    }

    public static Constants getConstants() {
        return constants;
    }

    public static Typeface getFontNormal() {
        return fontNormal;
    }

    public static Typeface getFontBold() {
        return fontBold;
    }

    public static Typeface getFontItalic() {
        return fontItalic;
    }

    public static void setTypeface(ViewGroup parent, Typeface typeface) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View view = parent.getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTypeface(typeface);
            } else if (view instanceof ViewGroup) {
                setTypeface((ViewGroup) view, typeface);
            }
        }
    }

    public RetroApi getRetroApi() {
        if (retroApi == null) {
            /*retroApi = ServiceGenerator.createService(RetroApi.class, constants.getUsername(),
                    constants.getPassword());*/
            retroApi = ServiceGenerator.createService(RetroApi.class);
        }
        return retroApi;
    }
}
