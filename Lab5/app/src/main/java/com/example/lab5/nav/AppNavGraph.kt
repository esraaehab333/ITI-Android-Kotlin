package com.example.lab5

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.lab5.dto.Product
import com.example.lab5.screens.DetailsScreen
import com.example.lab5.screens.MainScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    productList: List<Product>,
    isLoading: Boolean
) {
    NavHost(navController = navController, startDestination = "main") {

        composable("main") {
            MainScreen(
                productList = productList,
                isLoading = isLoading,
                onProductClick = { product ->
                    navController.navigate("details/${product.id}")
                }
            )
        }

        composable("details/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments
                ?.getString("productId")?.toIntOrNull()
            val product = productList.find { it.id == productId }
            product?.let {
                DetailsScreen(
                    product = it,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}