package com.example.lab5.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    // Gson cant handle the serialize the json
    // solution is give it the gson object
    // why that when have a null object convert it to null not crashed
    val gson:Gson = GsonBuilder().serializeNulls().create()
    private val retrofitInstance = Retrofit.Builder()
        .baseUrl("https://dummyjson.com/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    val apiService = retrofitInstance.create(ProductService::class.java)
}