package com.example.shoppinglist.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.shoppinglist.model.Item

@Database(entities = [Item::class], version = 1)
abstract class ItemDatabase: RoomDatabase() {
    abstract fun itemDao(): ItemDAO
}