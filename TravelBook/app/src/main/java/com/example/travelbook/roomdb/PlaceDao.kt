package com.example.travelbook.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.travelbook.model.Place
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface PlaceDao {

    @Query("SELECT * FROM Place")
    fun getAll() : Flowable<List<Place>>


    // INSERT INTO place (....) VALUES (....)
    @Insert
    fun insert (place: Place) : Completable


    @Delete
    fun delete (place: Place) : Completable

}