package com.example.eatstediapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase

@Database(entities = [Cart::class], version = 1, exportSchema = false)
abstract class CartRoomDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao?

    companion object {
        @Volatile
        private var INSTANCE: CartRoomDatabase?=null

        fun getDatabase(context: Context): CartRoomDatabase? {
            if (INSTANCE == null) {
                synchronized(CartRoomDatabase::class.java){
                    INSTANCE = databaseBuilder(context.applicationContext,
                        CartRoomDatabase::class.java, "favorite_database").build()
                }
            }
            return INSTANCE
        }
    }
}