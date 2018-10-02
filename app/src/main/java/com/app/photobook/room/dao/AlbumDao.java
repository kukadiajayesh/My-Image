package com.app.photobook.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.app.photobook.model.Album;

import java.util.List;

/**
 * Created by Jayesh on 11/29/2017.
 */

@Dao
public interface AlbumDao {

    @Insert
    void insert(Album album);

    @Delete
    void Delete(Album album);

    @Query("Select * from Album order by entryTime desc")
    List<Album> getAllAlbums();

    @Query("SELECT * FROM Album WHERE id = :id")
    List<Album> getAlbumId(int id);

    @Update
    void update(Album album);
}
