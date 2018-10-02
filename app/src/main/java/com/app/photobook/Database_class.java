package com.app.photobook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.app.photobook.tools.Constants;

import java.util.Date;


public class Database_class {

    SQLiteDatabase command;
    connection connection;
    Context context;

    public static String db_name = "photobook.db";

    String TABLE_NOTIFICATION = "CREATE TABLE IF NOT EXISTS notifications(id integer primary key autoincrement," +
            "json text,status int,entry_time long)";
    String TABLE_DROP_NOTIFICATION = "drop table IF EXISTS notifications;";

    class connection extends SQLiteOpenHelper {
        Context cnx;

        public connection(Context context, String name, CursorFactory factory,
                          int version) {
            super(context, name, factory, version);
            cnx = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            // create all table
            db.execSQL(TABLE_NOTIFICATION);
            // ==//
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL(TABLE_NOTIFICATION);

        }
    }

    public Database_class(Context context) {
        this.context = context;
    }

    public void close() {
        command.close();
        if (connection != null) {
            connection.close();
        }
    }

    public void open() {

        connection = new connection(context, db_name, null, 1);
        command = connection.getWritableDatabase();
    }

    public Cursor Select(String query) {
        return command.rawQuery(query, null);
    }

    public void Execute(String query) {
        command.execSQL(query);
    }

    public long save(final String json) {

        ContentValues params = new ContentValues();

        params.put("json", json);
        params.put("status", Constants.STATUS_NOTIFICATION_UNREAD);
        params.put("entry_time", new Date().getTime());

        long respo = command.insert("notifications", null, params);

        return respo;
    }

    public long delete(final String alert_id) {

        String[] where_args = {alert_id};

        String where = "id=?";
        return command.delete("notifications", where, where_args);
    }

    public long updateAsRead(int alert_id) {

        String[] where_args = {alert_id + ""};

        ContentValues params = new ContentValues();
        params.put("status", Constants.STATUS_NOTIFICATION_READ);

        String where = "id=?";
        return command.update("notifications", params, where, where_args);
    }


    public long deleteAll() {
        return command.delete("notifications", null, null);
    }


    public Cursor getNotifications() {
        return command.query("notifications", new String[]{}, null, null, null, null, "entry_time desc");
    }
    //==//

}

