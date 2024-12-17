package com.example.eatstediapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.eatstediapp.model.Menus

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inserCart(item: Cart)

    @Update
    fun updateCart(item: Cart)

    @Delete
    fun deleteCart(item: Cart)

    @Query("DELETE FROM menu WHERE product_name = :productName")
    fun deleteByProductName(productName: String)

    @Query ("SELECT * FROM menu ORDER BY id ASC")
    fun allCarts(): LiveData<List<Cart>>

    @Query("SELECT EXISTS(SELECT 1 FROM menu WHERE product_name = :product)")
    fun isCart(product: String): Boolean

    @Query("SELECT * FROM menu WHERE product_name = :productName LIMIT 1")
    fun getCartByProductName(productName: String): Cart?

}