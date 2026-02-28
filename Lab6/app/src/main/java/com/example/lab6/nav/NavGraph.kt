package com.example.lab6.nav
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.lab6.screens.HomeScreen
import com.example.lab6.screens.SignInScreen
import com.example.lab6.screens.SignUpScreen

@Composable
fun AppNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("signup") {
            SignUpScreen(navController)
        }

        composable("login") {
            SignInScreen(navController)
        }

        composable(
            route = "home/{username}",
            arguments = listOf(
                navArgument("username") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val username = backStackEntry.arguments?.getString("username") ?: ""

            HomeScreen(
                username = username,
                navController = navController
            )
        }
    }
}