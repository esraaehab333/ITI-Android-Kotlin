package com.example.lab5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.lab5.database.ProductDatabase
import com.example.lab5.dto.Product
import com.example.lab5.network.RetrofitHelper
import com.example.lab5.ui.theme.Lab5Theme
import com.example.lab5.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val dao = ProductDatabase.getInstance(this).productDao()
        val productListState = mutableStateOf<List<Product>>(emptyList())
        val isLoading = mutableStateOf(true)

        lifecycleScope.launch(Dispatchers.IO) {
            if (NetworkUtils.isConnected(this@MainActivity)) {
                val response = RetrofitHelper.apiService.getAllProducts()
                if (response.isSuccessful) {
                    val products = response.body()?.products ?: emptyList()
                    dao.insertProducts(products)
                    withContext(Dispatchers.Main) {
                        productListState.value = products
                        isLoading.value = false
                    }
                }
            } else {
                val products = dao.getAllProducts().first()
                withContext(Dispatchers.Main) {
                    productListState.value = products
                    isLoading.value = false
                }
            }
        }

        setContent {
            Lab5Theme {
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    productList = productListState.value,
                    isLoading = isLoading.value
                )
            }
        }
    }
}