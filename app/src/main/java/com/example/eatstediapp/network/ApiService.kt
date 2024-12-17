package com.example.eatstediapp.network

import com.example.eatstediapp.model.Menus
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("geuqT/menu")
    fun getAllMenu() : Call<List<Menus>>
}