package com.app.photobook.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.app.photobook.model.Album;
import com.app.photobook.model.AlbumImage;
import com.app.photobook.model.Photographer;
import com.app.photobook.room.dao.AlbumDao;
import com.app.photobook.room.dao.AlbumImageDao;
import com.app.photobook.room.dao.PhotographerDao;

/**
 * Created by Jayesh on 11/29/2017.
 */

@Database(entities = {Album.class, AlbumImage.class, Photographer.class}, version = 5)
public abstract class RoomDatabaseClass extends RoomDatabase {
    public abstract AlbumDao daoAlbum();

    public abstract AlbumImageDao daoAlbumImage();

    public abstract PhotographerDao daoPhotographer();
}
