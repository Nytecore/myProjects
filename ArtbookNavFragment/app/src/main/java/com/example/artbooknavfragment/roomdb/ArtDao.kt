package com.example.artbooknavfragment.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.artbooknavfragment.model.Art
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface ArtDao {

    @Query("SELECT name,id FROM Arts")
    fun getArtNameAndId(): Flowable<List<Art>>

    @Query("SELECT * FROM Arts WHERE id = :id")
    fun getArtById(id: Int): Flowable<Art>

    @Insert
    fun insert(vararg art: Art): Completable

    @Delete
    fun delete(art: Art): Completable

}