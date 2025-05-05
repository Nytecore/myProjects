package com.example.artbooknavfragment.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("Arts")
class Art (

    @ColumnInfo("name")
    var artName : String,

    @ColumnInfo("artistname")
    var artistName : String?,

    @ColumnInfo("year")
    var year : String?,

    @ColumnInfo("image")
    var image : ByteArray?

    ) {

    @PrimaryKey(autoGenerate = true)
    var id = 0

}