package com.bookintok.bookintokfront.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bookintok.bookintokfront.ui.screens.screens.HomeScreen
import com.bookintok.bookintokfront.ui.screens.screens.LocationScreen
import com.bookintok.bookintokfront.ui.screens.screens.LoginScreen
import com.bookintok.bookintokfront.ui.screens.screens.MainScreen
import com.bookintok.bookintokfront.ui.screens.screens.PointScreen
import com.bookintok.bookintokfront.ui.screens.screens.ProvinceScreen
import com.bookintok.bookintokfront.ui.screens.screens.RegisterScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(
            route = Screen.Home.route,
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(durationMillis = 1000)
                )
            },
            popExitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(durationMillis = 1000)
                )
            }
        ) {
            HomeScreen(navController = navController)
        }

        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }

        composable(Screen.Location.route) {
            LocationScreen(navController = navController)
        }
        composable(Screen.Point.route) {
            PointScreen(navController = navController)
        }
        composable(Screen.Province.route) {
            ProvinceScreen(navController = navController)
        }

        composable(Screen.Main.route) {
            MainScreen(navController = navController)
        }
    }
}