package com.bookintok.bookintokfront.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bookintok.bookintokfront.ui.screens.home.HomeScreen
import com.bookintok.bookintokfront.ui.screens.home.LocationScreen
import com.bookintok.bookintokfront.ui.screens.home.LoginScreen
import com.bookintok.bookintokfront.ui.screens.home.MainScreen
import com.bookintok.bookintokfront.ui.screens.home.PointScreen
import com.bookintok.bookintokfront.ui.screens.home.ProvinceScreen
import com.bookintok.bookintokfront.ui.screens.home.RegisterScreen
import com.bookintok.bookintokfront.ui.screens.home.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(route = Screen.Splash.route){
            SplashScreen(navController = navController)
        }
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
            LocationScreen(navController = navController,
                onPointSelected = { point ->
                    navController.navigate(Screen.Main.route)
                })
        }
        composable(Screen.Point.route) {
            PointScreen(
                onPointSelected = { point ->
                    navController.navigate(Screen.Main.route)
                },
                onBack = {
                    navController.navigateUp()
                }
            )
        }
        composable(Screen.Province.route) {
            ProvinceScreen(
                onPointSelected = { point ->
                    navController.navigate(Screen.Main.route)
                }
            )
        }

        composable(Screen.Main.route) {
            MainScreen(
                navController = navController
            )
        }
    }
}