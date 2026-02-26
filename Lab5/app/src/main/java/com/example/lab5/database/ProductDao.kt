package com.example.lab5.database

import androidx.room.*
import com.example.lab5.dto.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>)

    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<Product>>

    @Query("DELETE FROM products")
    suspend fun deleteAll()
}