package com.bookintok.bookintokfront.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bookintok.bookintokfront.ui.screens.BookDetailScreen
import com.bookintok.bookintokfront.ui.screens.BookEditScreen
import com.bookintok.bookintokfront.ui.screens.ChatScreen
import com.bookintok.bookintokfront.ui.screens.HomeScreen
import com.bookintok.bookintokfront.ui.screens.ListChatScreen
import com.bookintok.bookintokfront.ui.screens.LocationScreen
import com.bookintok.bookintokfront.ui.screens.LoginScreen
import com.bookintok.bookintokfront.ui.screens.MainScreen
import com.bookintok.bookintokfront.ui.screens.PointScreen
import com.bookintok.bookintokfront.ui.screens.ProfileScreen
import com.bookintok.bookintokfront.ui.screens.ProvinceScreen
import com.bookintok.bookintokfront.ui.screens.RegisterScreen

@RequiresApi(Build.VERSION_CODES.O)
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
        composable(
            Screen.ProfilePage.route, arguments = listOf(
                navArgument("userUid") {
                    type = NavType.StringType
                }
            )
        ) {
            ProfileScreen(
                navController = navController,
                uid = it.arguments?.getString("userUid").toString()
            )
        }


        composable(
            route = Screen.EditBook.route,
            arguments = listOf(
                navArgument("bookId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            BookEditScreen(
                navController = navController,
                bookId = it.arguments?.getString("bookId")
            )
        }

        composable(
            route = Screen.DetailBook.route,
            arguments = listOf(
                navArgument("bookId") {
                    type = NavType.StringType
                }
            )
        ) {
            BookDetailScreen(
                navController = navController,
                bookId = it.arguments?.getString("bookId").toString()
            )
        }

        composable(route = Screen.Chats.route) {
            ListChatScreen(navController = navController)
        }

        composable(
            route = Screen.Chat.route,
            arguments = listOf(
                navArgument("idChat") {
                    type = NavType.StringType
                }
            )
        ) {
            ChatScreen(
                navController = navController,
                idChat = it.arguments?.getString("idChat").toString()
            )
        }
    }
}