package com.example.eatstediapp.model

import com.google.gson.annotations.SerializedName

data class Menus(
    @SerializedName("_id")
    val id:String,
    @SerializedName("product_name")
    var name: String,
    @SerializedName("price")
    val price: String
)
