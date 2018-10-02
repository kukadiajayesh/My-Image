package com.app.photobook.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.app.photobook.model.AlbumImage;

import java.util.List;

/**
 * Created by Jayesh on 11/29/2017.
 */

@Dao
public interface AlbumImageDao {

    @Insert
    void insertAll(List<AlbumImage> albumImageBck);

    @Delete
    void Delete(AlbumImage photoAlbum);

    @Delete
    void DeleteAll(List<AlbumImage> photoAlbum);

    @Query("Select * from AlbumImage")
    List<AlbumImage> getAllAlbums();

    @Query("Select * from AlbumImage where albumId = :albumId")
    List<AlbumImage> getAllAlbums(int albumId);

    @Query("Select * from AlbumImage where selected = :value")
    List<AlbumImage> getAllSelectedImages(boolean value);

    @Update
    void update(AlbumImage photoAlbum);
}
