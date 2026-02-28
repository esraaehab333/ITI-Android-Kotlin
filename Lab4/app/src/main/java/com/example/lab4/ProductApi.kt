package com.example.lab4

import retrofit2.http.GET

interface ProductsApi {
    @GET("products")
    suspend fun getProducts(): ProductsResponseDto
}