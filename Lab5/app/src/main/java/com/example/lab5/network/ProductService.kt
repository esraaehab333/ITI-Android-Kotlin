package com.example.lab5.network

import com.example.lab5.dto.ProductResponse
import retrofit2.Response
import retrofit2.http.GET

interface ProductService {
    // not using the call back with corotine using the suspend
    @GET("products")
    suspend fun getAllProducts():Response<ProductResponse>
}