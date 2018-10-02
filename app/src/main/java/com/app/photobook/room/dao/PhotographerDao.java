package com.app.photobook.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.app.photobook.model.Photographer;

/**
 * Created by Jayesh on 11/29/2017.
 */

@Dao
public interface PhotographerDao {

    @Insert
    void insert(Photographer photographer);

    @Delete
    void delete(Photographer photographer);

    @Query("SELECT * FROM Photographer WHERE id= :id")
    Photographer getClientInfo(String id);

    @Query("SELECT * FROM Photographer")
    Photographer getClientInfo();

    @Query("DELETE FROM Photographer")
    void deleteAll();

    @Update
    void update(Photographer photographer);
}
