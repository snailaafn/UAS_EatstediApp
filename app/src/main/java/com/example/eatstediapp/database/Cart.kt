package com.example.eatstediapp.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "menu")
data class Cart (
    @PrimaryKey(autoGenerate = true)
    @NonNull
    val id: Int = 0,

    @ColumnInfo (name = "product_name")
    val productName: String,

//    @ColumnInfo (name = "price")
//    val price: String
)